package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.document.PostMonitor;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.repository.NewBoardRepository;
import com.huyeon.superspace.domain.board.repository.PopularBoardRepository;
import com.huyeon.superspace.domain.group.document.Group;
import com.huyeon.superspace.domain.group.repository.NewGroupRepository;
import com.huyeon.superspace.global.support.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularBoardService {
    private final CacheUtils cacheUtils;
    private final NewGroupRepository groupRepository;
    private final NewBoardRepository boardRepository;
    private final PopularBoardRepository popularBoardRepository;

    //redis에서 해당 그룹의 인기 게시글들을 가져옴
    public Map<String, Object> getPopularBoards(String url) {
        String key = String.format("%s:%s", url, "POPULAR_BOARD");
        return cacheUtils.findCache(key);
    }

    public void updatePopularPosts() {
        calculateViewsDiff();
        savePopularBoard();
    }

    //모든 게시글의 조회수 증가량을 계산
    public void calculateViewsDiff() {
        List<Board> boards = boardRepository.findAll();
        List<PostMonitor> monitors = new LinkedList<>();

        for (Board board : boards) {
            Long id = board.getId();
            String groupUrl = board.getGroupUrl();

            PostMonitor postMonitor = popularBoardRepository.findById(id)
                    .orElse(new PostMonitor(id, groupUrl));

            postMonitor.diffViews(board.getViews());

            monitors.add(postMonitor);
        }

        popularBoardRepository.saveAll(monitors);
    }

    //각 그룹별 게시글 중 조회수 인기게시글 redis에 저장
    public void savePopularBoard() {
        List<Group> groups = groupRepository.findAll();

        for (Group group : groups) {
            String key = String.format("%s:%s", group.getUrl(), "POPULAR_BOARD");
            List<PostMonitor> posts = popularBoardRepository.findPopularPostsInViews(group.getUrl());

            List<BoardDto> collect = posts.stream()
                    .map(post -> {
                        Long id = post.getId();
                        Board board = boardRepository.findById(id).orElseThrow();
                        return new BoardDto(board);
                    })
                    .collect(Collectors.toList());

            cacheUtils.saveCache(key, Map.of("VIEWS", collect));
            cacheUtils.setExpire(key, 2, TimeUnit.HOURS);
        }
    }
}

package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.document.LikePost;
import com.huyeon.superspace.domain.board.document.MemberLikePost;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.LikePostRes;
import com.huyeon.superspace.domain.board.repository.LikePostRepository;
import com.huyeon.superspace.domain.board.repository.MemberLikePostRepository;
import com.huyeon.superspace.domain.board.repository.NewBoardRepository;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.repository.NewMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikePostService {
    private final MemberLikePostRepository memberLikePostRepository;
    private final NewBoardRepository boardRepository;
    private final LikePostRepository likePostRepository;

    @Transactional
    public LikePostRes likeBoard(String memberId, Long boardId, String userEmail) {
        LikePost likePost = likePostRepository.findById(boardId).orElseThrow();
        MemberLikePost memberLikePost = memberLikePostRepository.findById(memberId)
                .orElse(new MemberLikePost(memberId));

        if (!likePost.contains(userEmail)) {
            likePost.like(userEmail);
            memberLikePost.addLikePost(boardId);
        } else {
            likePost.dislike(userEmail);
            memberLikePost.removeLikePost(boardId);
        }

        memberLikePostRepository.save(memberLikePost);
        likePostRepository.save(likePost);

        return new LikePostRes(likePost.getLike(), likePost.contains(userEmail));
    }

    @Transactional
    public int viewsUp(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        board.increaseView();
        return boardRepository.save(board).getViews();
    }

    @Transactional
    public void createLikePost(Long boardId) {
        LikePost likePost = new LikePost(boardId);
        likePostRepository.save(likePost);
    }

    @Transactional(readOnly = true)
    public List<BoardDto> getNextLike(String memberId, Long lastIndex, int page) {
        MemberLikePost memberLikePost = memberLikePostRepository.findById(memberId)
                .orElse(new MemberLikePost(memberId));

        return memberLikePost.getNextLikes(lastIndex, page)
                .stream()
                .map(likePost -> {
                    Long postId = likePost.getPostId();
                    Board board = boardRepository.findById(postId).orElseThrow();
                    return new BoardDto(board);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LikePostRes getBoardLikes(String userEmail, Long boardId) {
        LikePost likePost = findLikePost(boardId);
        return new LikePostRes(likePost.getLike(), likePost.contains(userEmail));
    }

    private LikePost findLikePost(Long boardId) {
        return likePostRepository.findById(boardId).orElseThrow();
    }
}

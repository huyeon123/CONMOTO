package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.history.UserHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.ContentBlockRepository;
import com.huyeon.apiserver.repository.UserRepository;
import com.huyeon.apiserver.repository.history.UserHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.huyeon.apiserver.support.JsonParse.readJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ContentBlockRepository blockRepository;
    private final CommentRepository commentRepository;
    private final UserHistoryRepo userHistoryRepo;

    //회원정보 수정
    public boolean editInfo(String email, String editForm) {
        User editUser = readJson(editForm, User.class);

        if (editUser != null && editUser.getEmail().equals(email)) {
            userRepository.save(editUser);
            return true;
        }
        return false;
    }

    //회원탈퇴
    @Transactional
    public boolean resign(String email) {
        userRepository.deleteById(email);
        return true;
    }

    //게시글 확인
    public List<Board> myBoard(String email) {
        Optional<List<Board>> boards =
                boardRepository.findAllByUserEmail(email);
        return boards.orElseGet(List::of);
    }

    //게시글&내용 확인
    public Map<Board, List<ContentBlock>> myBoardAndContents(String email) {
        Map<Board, List<ContentBlock>> boardAndContents = new LinkedHashMap<>();
        List<Board> boards = myBoard(email);
        boards.forEach(board -> {
            List<ContentBlock> contents = blockRepository.findAllByBoardId(board.getId()).orElseGet(List::of);
            boardAndContents.put(board, contents);
        });
        return boardAndContents;
    }

    //댓글 확인
    public List<Comment> myComment(String email) {
        Optional<List<Comment>> optionalComments =
                commentRepository.findAllByUserEmail(email);

        //탈퇴한 사용자 게시글은 제외
        return optionalComments.map(comments -> comments
                .stream()
                .filter(comment -> userRepository.existsByEmail(comment.getUserEmail()))
                .collect(Collectors.toList())).orElseGet(List::of);
    }

    //회원정보 수정이력 확인
    public List<UserHistory> myInfoHistory(String email) {
        Optional<List<UserHistory>> histories =
                userHistoryRepo.findAllByUserEmail(email);
        return histories.orElseGet(List::of);
    }
}

package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.model.dto.history.UserHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.UserRepository;
import com.huyeon.apiserver.repository.history.UserHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.huyeon.apiserver.support.JsonParse.readJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserHistoryRepo userHistoryRepo;

    //회원가입
    public boolean signUp(String signUpForm) {
        User user = readJson(signUpForm, User.class);
        if (user == null) return false;
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isEmpty()) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    //회원정보
    public User userInfo(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(new User());
    }

    //회원정보 수정
    public boolean editInfo(Long id, String editForm) {
        Optional<User> optionalUser = userRepository.findById(id);
        User currentUser = optionalUser.orElse(new User());

        User editUser = readJson(editForm, User.class);

        //요청보낸 사용자와 수정정보 ID가 일치할때만 반영 => Security 작업할 때 보완
        if (editUser != null
                && editUser.getId().equals(currentUser.getId())) {
            userRepository.save(editUser);
            return true;
        }
        return false;
    }

    //회원탈퇴
    @Transactional
    public boolean resign(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
//            boardRepository.deleteAllByUserId(id);
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //게시글 확인
    public List<Board> myBoard(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Optional<List<Board>> boards =
                    boardRepository.findAllByUserId(optionalUser.get().getId());
            if (boards.isPresent()) return boards.get();
        }
        return List.of();
    }

    //댓글 확인
    public List<Comment> myComment(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Optional<List<Comment>> optionalComments =
                    commentRepository.findAllByUserId(optionalUser.get().getId());
            if (optionalComments.isPresent()) {
                //탈퇴한 사용자 게시글은 제외
                return optionalComments.get()
                        .stream()
                        .filter(comment -> {
                            Long boardId = comment.getBoardId();
                            Optional<Board> board = boardRepository.findById(boardId);
                            return board.isPresent();
                        })
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    //회원정보 수정이력 확인
    public List<UserHistory> myInfoHistory(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<List<UserHistory>> histories =
                    userHistoryRepo.findAllByUserId(user.get().getId());
            if (histories.isPresent()) return histories.get();
        }
        return List.of();
    }
}

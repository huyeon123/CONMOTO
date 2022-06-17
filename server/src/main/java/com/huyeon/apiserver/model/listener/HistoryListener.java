package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.ContentBlock;
import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.model.dto.history.BoardHistory;
import com.huyeon.apiserver.model.dto.history.CommentHistory;
import com.huyeon.apiserver.model.dto.history.ContentBlockHistory;
import com.huyeon.apiserver.model.dto.history.UserHistory;
import com.huyeon.apiserver.repository.history.BoardHistoryRepo;
import com.huyeon.apiserver.repository.history.CommentHistoryRepo;
import com.huyeon.apiserver.repository.history.ContentBlockHistoryRepo;
import com.huyeon.apiserver.repository.history.UserHistoryRepo;
import com.huyeon.apiserver.support.BeanUtils;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class HistoryListener {
    @PostPersist
    @PostUpdate
    public void test(Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.equals(Board.class)) {
            BoardHistoryRepo historyRepository = BeanUtils.getBean(BoardHistoryRepo.class);

            Board board = (Board) o;
            BoardHistory boardHistory = BoardHistory.builder()
                    .boardId(board.getId())
                    .userId(board.getUserId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .build();

            boardHistory.setCreatedAt(board.getCreatedAt());

            historyRepository.save(boardHistory);
        } else if (clazz.equals(User.class)) {
            UserHistoryRepo userHistoryRepo = BeanUtils.getBean(UserHistoryRepo.class);

            User user = (User) o;
            UserHistory userHistory = UserHistory.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .isDelete(false)
                    .build();

            userHistory.setCreatedAt(user.getCreatedAt());

            userHistoryRepo.save(userHistory);
        } else if (clazz.equals(ContentBlock.class)) {
            ContentBlockHistoryRepo contentBlockHistoryRepo = BeanUtils.getBean(ContentBlockHistoryRepo.class);

            ContentBlock contentBlock = (ContentBlock) o;
            ContentBlockHistory contentBlockHistory = ContentBlockHistory.builder()
                    .blockId(contentBlock.getId())
                    .boardId(contentBlock.getBoardId())
                    .content(contentBlock.getContent())
                    .build();

            contentBlockHistory.setCreatedAt(contentBlock.getCreatedAt());

            contentBlockHistoryRepo.save(contentBlockHistory);
        } else if (clazz.equals(Comment.class)) {
            CommentHistoryRepo commentHistoryRepo = BeanUtils.getBean(CommentHistoryRepo.class);

            Comment comment = (Comment) o;
            CommentHistory commentHistory = CommentHistory.builder()
                    .commentId(comment.getId())
                    .boardId(comment.getBoardId())
                    .userId(comment.getUserId())
                    .comment(comment.getComment())
                    .build();

            commentHistory.setCreatedAt(comment.getCreatedAt());

            commentHistoryRepo.save(commentHistory);
        }

    }
}
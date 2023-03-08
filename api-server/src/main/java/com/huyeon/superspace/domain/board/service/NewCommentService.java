package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.document.Comment;
import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.CommentPreviewDto;
import com.huyeon.superspace.domain.board.repository.NewBoardRepository;
import com.huyeon.superspace.domain.board.repository.NewCommentRepository;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import com.huyeon.superspace.domain.group.repository.NewMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewCommentService {
    private final NewCommentRepository commentRepository;
    private final NewMemberRepository memberRepository;
    private final NewBoardRepository boardRepository;

    public List<CommentDto> getCommentInBoard(String groupUrl, String userEmail, Long key, int page) {
        List<Comment> comments = commentRepository.findAllByBoardIdOrderByCreatedAt(key, PageRequest.of(page, 10));

        return comments.stream()
                .map(comment -> mapToDto(comment, groupUrl, userEmail))
                .collect(Collectors.toList());
    }

    private CommentDto mapToDto(Comment comment, String groupId, String userEmail) {
        String authorEmail = comment.getAuthor();
        String nickname = memberRepository.findByGroupUrlAndUserEmail(groupId, authorEmail)
                .orElseThrow()
                .getNickname();
        boolean mine = userEmail.equals(authorEmail);
        return new CommentDto(comment, nickname, mine);
    }

    public List<CommentPreviewDto> getNextComment(String memberId, Long lastIndex, int page) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        List<Comment> next = findNextComments(member.getUserEmail(), member.getGroupUrl(), lastIndex, page);

        return next.stream()
                .map(comment -> {
                    Board board = boardRepository.findById(comment.getBoardId()).orElseThrow();
                    return new CommentPreviewDto(comment, board);
                })
                .collect(Collectors.toList());
    }

    private List<Comment> findNextComments(String userEmail, String groupUrl, Long lastIndex, int page) {
        return commentRepository.findNextByUserEmail(
                userEmail,
                groupUrl,
                lastIndex,
                PageRequest.of(page, 50));
    }

    public String createComment(String userEmail, CommentDto request) {
        Comment comment = new Comment(request);
        comment.setAuthor(userEmail);
        return commentRepository.save(comment).getId();
    }

    public Long editComment(String userEmail, CommentDto request) {
        Comment origin = commentRepository.findById(request.getId()).orElseThrow();

        if (!origin.getAuthor().equals(userEmail)) {
            throw new PermissionDeniedException("작성자 본인만 수정할 수 있습니다!");
        }

        origin.setBody(request.getBody());
        origin.setTag(request.getTag());
        return commentRepository.save(origin).getId();
    }

    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }
}

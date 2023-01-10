package com.huyeon.superspace.domain.newboard.service;

import com.huyeon.superspace.domain.newboard.document.Comment;
import com.huyeon.superspace.domain.newboard.dto.CommentDto;
import com.huyeon.superspace.domain.newboard.repository.NewCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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


    public List<CommentDto> getCommentInUser(String userEmail, int page) {
        List<Comment> latest = getNextComments(userEmail, page);
        return mapToDtoList(latest);
    }

    private List<Comment> getNextComments(String userEmail, int page) {
        return commentRepository.findNextByUserEmail(
                userEmail,
                LocalDateTime.now(),
                PageRequest.of(page, 10));
    }

    private List<CommentDto> mapToDtoList(List<Comment> documentList) {
        return documentList.stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    public String createComment(String userEmail, CommentDto request) {
        Comment comment = new Comment(request);
        comment.setAuthor(userEmail);
        return commentRepository.save(comment).getId();
    }

    public String editComment(String userEmail, CommentDto request) {
        Comment origin = commentRepository.findById(request.getId()).orElseThrow();

        if (!origin.getAuthor().equals(userEmail)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }

        origin.setBody(request.getBody());
        origin.setTag(request.getTag());
        return commentRepository.save(origin).getId();
    }

    public void removeComment(String id) {
        commentRepository.deleteById(id);
    }
}

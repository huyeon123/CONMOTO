package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Getter
@Setter
@Document(collection = "post_monitor")
public class PostMonitor extends DocumentAudit {
    @Id
    private Long id;

    private String groupUrl;

    private int prevViews;

    private int viewDiff;

    private int prevLike;

    private int likeDiff;

    private int prevComment;

    private int commentDiff;

    public PostMonitor(Long id, String groupUrl) {
        this.id = id;
        this.groupUrl = groupUrl;
        this.prevViews = 0;
        this.prevLike = 0;
        this.prevComment = 0;
    }

    public void diffViews(int currentViews) {
        viewDiff = prevViews - currentViews;
        prevViews = currentViews;
    }

    public void diffLike(int currentLike) {
        viewDiff = prevLike - currentLike;
    }

    public void diffComment(int currentComment) {
        viewDiff = prevComment - currentComment;
    }
}

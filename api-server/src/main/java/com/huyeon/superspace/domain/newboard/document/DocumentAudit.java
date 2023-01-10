package com.huyeon.superspace.domain.newboard.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentAudit {
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

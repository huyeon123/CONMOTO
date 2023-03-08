package com.huyeon.superspace.domain.group.document;

import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "members")
public class Member extends DocumentAudit {
    @Id
    private String id;

    @Field("group_url")
    @Indexed(unique = true)
    private String groupUrl;

    @Field("user_email")
    @Indexed(unique = true)
    private String userEmail;

    private String nickname;

    private String role;

    @Field("grade_level")
    private int gradeLevel;

    @Field("post_count")
    private int postCount;

    @Field("comment_count")
    private int commentCount;
}

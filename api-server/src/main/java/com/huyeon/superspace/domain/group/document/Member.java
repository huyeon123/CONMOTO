package com.huyeon.superspace.domain.group.document;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "members")
public class Member {
    @DBRef
    @Indexed(unique = true)
    private Group group;

    @Indexed(unique = true)
    private String userEmail;

    private String nickname;

    private String role;
}

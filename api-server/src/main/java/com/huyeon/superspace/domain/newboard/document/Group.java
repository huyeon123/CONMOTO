package com.huyeon.superspace.domain.newboard.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "groups")
public class Group extends DocumentAudit {
    @Id
    private String id;

    private String owner;

    private String url;

    private String description;

    private String[] managers;
}

package com.huyeon.superspace.domain.group.document;

import com.huyeon.superspace.domain.group.dto.CreateDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "groups")
public class Group extends DocumentAudit {
    @Id
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String owner;

    @NonNull
    private String url;

    private String description;

    private int membersNum;

    private Set<String> managers;

    private boolean open;

    private boolean autoJoin;

    public Group(GroupDto dto) {
        id = dto.getId();
        name = dto.getName();
        owner = dto.getOwner();
        url = dto.getUrl();
        description = dto.getDescription();
        membersNum = dto.getMembersNum();
        managers = dto.getManagers();
    }

    public Group(String ownerEmail, CreateDto request) {
        name = request.getGroupName();
        owner = ownerEmail;
        url = request.getGroupUrl();
        description = request.getDescription();
    }
}

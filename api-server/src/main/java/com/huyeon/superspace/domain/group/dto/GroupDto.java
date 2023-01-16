package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.document.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class GroupDto {
    private String id;

    private String name;

    private String owner;

    private String url;

    private String description;

    private int membersNum;

    private Set<String> managers;

    public GroupDto(Group group) {
        id = group.getId();
        name = group.getName();
        owner = group.getOwner();
        url = group.getUrl();
        description = group.getDescription();
        membersNum = group.getMembersNum();
        managers = group.getManagers();
    }
}

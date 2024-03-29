package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.document.Group;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupViewDto {
    private String groupId;

    private String groupName;

    private String groupUrl;

    private String description;

    private int memberNum;

    private String myNickname;

    private String myRole;

    public GroupViewDto(GroupDto group, Member member) {
        groupId = group.getId();
        groupName = group.getName();
        groupUrl = group.getUrl();
        description = group.getDescription();
        memberNum = group.getMembersNum();
        myNickname = member.getNickname();
        myRole = member.getRole();
    }
}

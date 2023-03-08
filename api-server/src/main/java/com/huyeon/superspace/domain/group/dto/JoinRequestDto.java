package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.document.JoinRequest;
import com.huyeon.superspace.domain.group.document.Requester;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {
    private String id;

    private String groupUrl;

    private List<Requester> requesters;

    public JoinRequestDto(JoinRequest joinRequest) {
        this.id = joinRequest.getId();
        this.groupUrl = joinRequest.getGroupUrl();
        this.requesters = joinRequest.getRequesters();
    }

    public void removeRequester(Requester requester) {
        requesters.remove(requester);
    }

}

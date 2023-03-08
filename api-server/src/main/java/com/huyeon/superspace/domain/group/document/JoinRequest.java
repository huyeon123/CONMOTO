package com.huyeon.superspace.domain.group.document;

import com.huyeon.superspace.domain.group.document.Requester;
import com.huyeon.superspace.domain.group.dto.JoinDto;
import com.huyeon.superspace.domain.group.dto.JoinRequestDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "join_request")
public class JoinRequest extends DocumentAudit {
    @Id
    private String id;

    @Field("group_url")
    private String groupUrl;

    private List<Requester> requesters;

    public JoinRequest(String groupUrl) {
        this.groupUrl = groupUrl;
        this.requesters = new LinkedList<>();
    }

    public JoinRequest(JoinRequestDto joinRequestList) {
        this.id = joinRequestList.getId();
        this.groupUrl = joinRequestList.getGroupUrl();
        this.requesters = joinRequestList.getRequesters();
    }

    public void addRequester(JoinDto request) {
        requesters.add(new Requester(request));
    }

}

package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.group.repository.NewGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCheckService {
    private final NewGroupRepository groupRepository;

    //작성자이거나 관리자일 경우 수정 허용
    public boolean isGrantAccess(BoardDto boardDto, String userEmail) {
        return isAuthor(boardDto.getAuthor(), userEmail)
                || isManager(boardDto.getGroupUrl(), userEmail);
    }

    private boolean isAuthor(String author, String userEmail) {
        return author.equals(userEmail);
    }

    private boolean isManager(String groupUrl, String userEmail) {
        return groupRepository.findByUrl(groupUrl)
                .orElseThrow()
                .getManagers()
                .contains(userEmail);
    }
}

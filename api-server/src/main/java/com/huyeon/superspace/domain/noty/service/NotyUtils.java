package com.huyeon.superspace.domain.noty.service;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.repository.NewBoardRepository;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.repository.NewGroupRepository;
import com.huyeon.superspace.domain.group.repository.NewMemberRepository;
import com.huyeon.superspace.domain.noty.dto.NotyPayloadDto;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.global.dto.NotyEventDto;
import com.huyeon.superspace.global.model.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotyUtils {
    private final NewGroupRepository groupRepository;
    private final NewMemberRepository memberRepository;
    private final NewBoardRepository boardRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishNoty(NotyPayloadDto payload) {
        final Noty noty = createExactNoty(payload);

        assert Objects.nonNull(noty);

        List<ReceivedNoty> receivers = initializeReceive(noty, payload);
        NotyEventDto notyEvent = createNotyEvent(noty, receivers);
        publishEvent(notyEvent);
    }

    private Noty createExactNoty(NotyPayloadDto payload) {
        GroupDto group = getGroup(payload);

        switch (payload.getType()) {
            case GROUP_INVITE:
                return createInviteNoty(group, payload.getRequesterEmail());
            case NOTICE:
                return createNoticeNoty(group, payload.getBoard());
            case BOARD_NEW_COMMENT:
                return createNewCommentNoty(group, payload.getComment());
            case GROUP_ROLE_CHANGE:
                return createRoleChangeNoty(group, payload.getData());
            case GROUP_EXPEL:
                return createExpelNoty(group, payload.getData());
            default:
                return null;
        }
    }

    private GroupDto getGroup(NotyPayloadDto payload) {
        if (Objects.nonNull(payload.getGroup())) return payload.getGroup();

        return groupRepository.findByUrl(payload.getGroupUrl())
                .map(GroupDto::new)
                .orElseThrow();
    }

    private Noty createInviteNoty(GroupDto group, String requester) {
        return Noty.builder()
                .type(NotyType.GROUP_INVITE)
                .title("그룹 초대 메세지가 있습니다.")
                .body(group.getDescription())
                .payload(group.getUrl())
                .senderName(requester)
                .groupName(group.getName())
                .build();
    }

    private Noty createNoticeNoty(GroupDto group, BoardDto board) {
        return Noty.builder()
                .type(NotyType.NOTICE)
                .title("새로운 공지사항이 있습니다.")
                .body(board.getTitle())
                .redirectUrl(board.getUrl())
                .groupName(group.getName())
                .build();
    }

    private Noty createNewCommentNoty(GroupDto group, CommentDto comment) {
        BoardDto board = getBoard(comment);

        return Noty.builder()
                .type(NotyType.BOARD_NEW_COMMENT)
                .title("새로운 댓글이 있습니다.")
                .body(comment.getBody())
                .payload(board.getTitle())
                .redirectUrl(board.getUrl())
                .senderName(comment.getAuthor())
                .groupName(group.getName())
                .build();
    }

    private BoardDto getBoard(CommentDto comment) {
        return boardRepository.findById(comment.getBoardId())
                .map(BoardDto::new)
                .orElseThrow();
    }

    private Noty createRoleChangeNoty(GroupDto group, String gradeName) {
        return Noty.builder()
                .type(NotyType.GROUP_ROLE_CHANGE)
                .title("등급이 변경되었습니다.")
                .body('\'' + gradeName + '\'' + "으로 변경되었습니다.")
                .groupName(group.getName())
                .build();
    }

    private Noty createExpelNoty(GroupDto group, String expelReason) {
        return Noty.builder()
                .type(NotyType.GROUP_EXPEL)
                .title("그룹에서 강제 탈퇴되었습니다.")
                .body(expelReason)
                .groupName(group.getName())
                .build();
    }

    private List<ReceivedNoty> initializeReceive(Noty noty, NotyPayloadDto payload) {
        if (payload.getType().equals(NotyType.NOTICE)) {
            return getMemberList(payload.getGroupUrl()).stream()
                    .map(email -> createReceiver(email, noty))
                    .collect(Collectors.toList());
        } else {
            return List.of(createReceiver(payload.getReceiverEmail(), noty));
        }
    }

    private List<String> getMemberList(String groupUrl) {
        return memberRepository.findAllByGroupUrl(groupUrl)
                .stream()
                .map(Member::getUserEmail)
                .collect(Collectors.toList());
    }

    private ReceivedNoty createReceiver(String receiverEmail, Noty noty) {
        return ReceivedNoty.builder()
                .userEmail(receiverEmail)
                .noty(noty)
                .build();
    }

    private NotyEventDto createNotyEvent(Noty noty, List<ReceivedNoty> receivers) {
        return NotyEventDto.builder()
                .noty(noty)
                .receivers(receivers)
                .build();
    }

    private void publishEvent(NotyEventDto notyEvent) {
        EventPublisher.publishEvent(eventPublisher, notyEvent);
    }
}

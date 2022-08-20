package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.MyEvent;
import com.huyeon.apiserver.model.dto.GroupDto;
import com.huyeon.apiserver.model.entity.*;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupManagerRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final GroupManagerRepository managerRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int IDENTIFIER_LENGTH = 10;

    public Groups getGroupByUrl(String urlPath) {
        return groupRepository.findByUrlPath(urlPath).orElseThrow();
    }

    public String getGroupNameByUrl(String urlPath) {
        return groupRepository.findByUrlPath(urlPath).orElseThrow().getName();
    }

    public List<Groups> getGroups(User user) {
        List<UserGroup> userGroups = userGroupRepository.findAllByUser(user).orElseGet(List::of);
        return userGroups.stream().map(UserGroup::getGroup).collect(Collectors.toList());
    }

    public List<User> getUsers(Groups group) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group).orElseGet(List::of);
        return userGroups.stream().map(UserGroup::getUser).collect(Collectors.toList());
    }

    public Groups createGroup(User user, GroupDto request) {
        Groups group = Groups.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .build();

        String urlPath = request.getUrl();

        if (exist(urlPath)) {
            urlPath = appendIdentifier(urlPath);
        }

        group.setUrlPath(urlPath);

        return groupRepository.save(group);
    }

    public boolean exist(String urlPath) {
        return groupRepository.existsByUrlPath(urlPath);
    }

    private String appendIdentifier(String urlPath) {
        StringBuilder sb = new StringBuilder(urlPath);
        sb.append("-");
        sb.append(generateIdentifier());
        return sb.toString();
    }

    private String generateIdentifier() {
        int leftLimit = '0'; // numeral '0'
        int rightLimit = 'z'; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(IDENTIFIER_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public boolean editGroup(String groupUrl, GroupDto request) {
        Groups group = getGroupByUrl(groupUrl);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        groupRepository.save(group);
        return true;
    }

    @Transactional
    public String deleteGroup(User user, String groupUrl) {
        Groups group = getGroupByUrl(groupUrl);
        if (notOnlyMeInGroup(group)) {
            return "다른 멤버가 존재합니다.";
        }
        if (isNotGroupOwner(user, group)) {
            return "그룹 소유자가 아닙니다.";
        }
        List<GroupManager> groupManager = managerRepository.findAllByGroup(group).orElseThrow();

        categoryRepository.deleteAllByGroup(group);
        userGroupRepository.deleteAllByGroup(group);
        managerRepository.deleteAll(groupManager);
        groupRepository.delete(group);
        return "삭제되었습니다.";
    }

    private boolean notOnlyMeInGroup(Groups group) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group).orElseThrow();
        return userGroups.size() > 1;
    }

    private boolean isNotGroupOwner(User user, Groups group) {
        return !group.getOwner().equals(user);
    }

    public String checkRole(Groups group, User user) {
        if (isOwner(group, user)) {
            return "그룹 소유자";
        }
        if (isManager(group, user)) {
            return "그룹 관리자";
        }

        return "일반 멤버";
    }

    private boolean isOwner(Groups group, User user) {
        return group.getOwner().equals(user);
    }

    private boolean isManager(Groups group, User user) {
        return managerRepository.existsByGroupAndManager(group, user);
    }

    public void inviteMember(Groups group, String userEmail) {
        Noty inviteNoty = Noty.builder()
                .senderName(group.getName())
                .receivers(List.of(userEmail))
                .message("그룹에 초대합니다!\n" + group.getDescription())
                .type(NotyType.GROUP_INVITE)
                .build();

        MyEvent.publishEvent(eventPublisher, inviteNoty);
    }
}

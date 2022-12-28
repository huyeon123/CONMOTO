package com.huyeon.superspace.domain.group.service;

import com.huyeon.superspace.domain.board.repository.CategoryRepository;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.entity.GroupManager;
import com.huyeon.superspace.domain.group.entity.UserGroup;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.exception.NotAdminException;
import com.huyeon.superspace.domain.group.exception.NotOnlyMemberException;
import com.huyeon.superspace.domain.group.repository.GroupManagerRepository;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import com.huyeon.superspace.domain.group.repository.UserGroupRepository;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {
    private static final int IDENTIFIER_LENGTH = 10;

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final GroupManagerRepository managerRepository;
    private final CategoryRepository categoryRepository;

    public GroupDto createGroup(String email, GroupDto request) {
        User user = findUserByEmail(email);

        WorkGroup group = makeGroup(user, request);

        registerUser(user, group);

        registerUserAsManager(user, group);

        return mapToDto(group);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    private WorkGroup makeGroup(User user, GroupDto request) {
        WorkGroup group = WorkGroup.builder()
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

    private void registerUser(User user, WorkGroup group) {
        UserGroup userGroup = new UserGroup(user, group);
        userGroupRepository.save(userGroup);
    }

    private void registerUserAsManager(User user, WorkGroup group) {
        GroupManager groupManager = new GroupManager(user, group);
        managerRepository.save(groupManager);
    }

    private GroupDto mapToDto(WorkGroup group) {
        return new GroupDto(group);
    }

    public void editGroup(String groupUrl, GroupDto request) {
        WorkGroup group = getGroupByUrl(groupUrl);
        group.setName(request.getName());
        group.setUrlPath(request.getUrl());
        group.setDescription(request.getDescription());
        groupRepository.save(group);
    }

    public WorkGroup getGroupByUrl(String urlPath) {
        return groupRepository.findByUrlPath(urlPath).orElseThrow();
    }

    public String getGroupNameByUrl(String urlPath) {
        return groupRepository.findByUrlPath(urlPath).orElseThrow().getName();
    }

    public List<WorkGroup> getGroups(String email) {
        List<UserGroup> userGroups = userGroupRepository.findGroupsByEmail(email);
        return userGroups.stream().map(UserGroup::getGroup).collect(Collectors.toList());
    }

    public List<User> getUsers(WorkGroup group) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
        return userGroups.stream().map(UserGroup::getUser).collect(Collectors.toList());
    }

    private boolean exist(String urlPath) {
        return groupRepository.existsByUrlPath(urlPath);
    }

    private String appendIdentifier(String urlPath) {
        return urlPath + "-" + generateIdentifier();
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

    public void deleteGroup(String email, String groupUrl) {
        WorkGroup group = getGroupByUrl(groupUrl);

        if (notOnlyMeInGroup(group)) {
            throw new NotOnlyMemberException("다른 멤버가 존재합니다.");
        }

        if (isNotGroupOwner(email, group)) {
            throw new NotAdminException("그룹 소유자가 아닙니다.");
        }

        List<GroupManager> groupManager = managerRepository.findAllByGroup(group);

        categoryRepository.deleteAllByGroup(group);
        userGroupRepository.deleteAllByGroup(group);
        managerRepository.deleteAll(groupManager);
        groupRepository.delete(group);
    }

    private boolean notOnlyMeInGroup(WorkGroup group) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroup(group);
        return userGroups.size() > 1;
    }

    private boolean isNotGroupOwner(String email, WorkGroup group) {
        return !group.getOwner().getEmail().equals(email);
    }

    public String checkRole(WorkGroup group, User user) {
        if (isOwner(group, user)) {
            return "그룹 소유자";
        }
        if (isManager(group, user)) {
            return "그룹 관리자";
        }

        return "일반 멤버";
    }

    private boolean isOwner(WorkGroup group, User user) {
        return group.getOwner().equals(user);
    }

    private boolean isManager(WorkGroup group, User user) {
        return managerRepository.existsByGroupAndManager(group, user);
    }
}

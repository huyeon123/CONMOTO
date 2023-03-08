package com.huyeon.superspace.domain.user.service;

import com.huyeon.superspace.domain.user.dto.EditRes;
import com.huyeon.superspace.domain.user.dto.UserUpdateDto;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import com.huyeon.superspace.domain.user.repository.UserHistoryRepo;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.web.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserHistoryRepo userHistoryRepo;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUser(String email) {
        return new UserDto(findUser(email));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    public void editInfo(String email, UserUpdateDto editUser) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (Objects.nonNull(editUser.getName())) user.setName(editUser.getName());
            if (Objects.nonNull(editUser.getPassword())) {
                user.setPassword(editUser.getPassword());
                user.encryptPassword(passwordEncoder);
            }
            if (Objects.nonNull(editUser.getBirthday())) user.setBirthday(editUser.getBirthday());
            userRepository.save(user);
        });
    }

    public void resign(String email) {
        User user = findUser(email);
        user.setExpireDate(LocalDate.now().plusDays(15));
        save(user);
    }

    public void cancelResign(String email) {
        User user = findUser(email);

        if (Objects.isNull(user.getExpireDate())) return;

        user.setEnabled(true);
        user.setExpireDate(null);
        userRepository.save(user);
    }

    public List<UserHistory> myInfoHistory(String email) {
        return userHistoryRepo.findAllByUserEmail(email);
    }
}

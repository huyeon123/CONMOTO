package com.huyeon.superspace.domain.user.service;

import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import com.huyeon.superspace.domain.user.repository.UserHistoryRepo;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.web.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserHistoryRepo userHistoryRepo;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return new UserDto(user);
    }

    public void editInfo(String email, User editUser) {
        if (editUser.getEmail().equals(email)) {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setName(editUser.getName());
                user.setPassword(editUser.getPassword());
                user.setBirthday(editUser.getBirthday());
                user.encryptPassword(passwordEncoder);
                userRepository.save(user);
            });
        }
    }

    public void resign(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setEnabled(false);
            user.setExpireDate(LocalDate.now().plusDays(15));
            userRepository.save(user);
        });
    }

    public void cancelResign(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.isEnabled()) {
                user.setEnabled(true);
                user.setExpireDate(null);
                userRepository.save(user);
            }
        });
    }

    public List<UserHistory> myInfoHistory(String email) {
        return userHistoryRepo.findAllByUserEmail(email);
    }
}

package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.UserDto;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.history.UserHistory;
import com.huyeon.apiserver.repository.UserRepository;
import com.huyeon.apiserver.repository.history.UserHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserHistoryRepo userHistoryRepo;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserDto(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new UserDto(user);
    }

    //회원정보 수정
    public boolean editInfo(String email, User editUser) {
        if (editUser.getEmail().equals(email)) {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setName(editUser.getName());
                user.setPassword(editUser.getPassword());
                user.setBirthday(editUser.getBirthday());
                user.encryptPassword(passwordEncoder);
                userRepository.save(user);
            });
            return true;
        }
        return false;
    }

    //회원탈퇴
    public boolean resign(String email) {
        try {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setExpireDate(LocalDate.now().plusDays(15));
                userRepository.save(user);
            });
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    //회원탈퇴 취소
    public boolean cancelResign(String email) {
        try {
            userRepository.findByEmail(email).ifPresent(user -> {
                if (!user.isEnabled()) {
                    user.setEnabled(true);
                    user.setExpireDate(null);
                    userRepository.save(user);
                }
            });
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    //회원정보 수정이력 확인
    public List<UserHistory> myInfoHistory(String email) {
        return userHistoryRepo.findAllByUserEmail(email);
    }
}

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

    public EditRes editName(String email, UserUpdateDto request) {
        User user = findUser(email);

        if (Objects.isNull(request.getName())) {
            return new EditRes("바꿀 이름은 null일 수 없습니다.");
        }

        user.setName(request.getName());
        save(user);

        return new EditRes();
    }

    public void editBirthday(String email, UserUpdateDto request) {
        User user = findUser(email);
        user.setBirthday(request.getBirthday());
        save(user);
    }

    public EditRes editPassword(String email, UserUpdateDto request) {
        User user = findUser(email);

        String current = request.getCurrent();
        String password = request.getPassword();
        String again = request.getAgain();

        if (!password.equals(again)) {
            return new EditRes("바꿀 비밀번호가 일치하지 않습니다.");
        }

        String encode = passwordEncoder.encode(current);

        if (!user.getPassword().equals(encode)) {
            //Redis에서 임시 비밀번호 가져오기
            String loginCode = redisTemplate.opsForValue().get("loginCode:" + email);
            assert loginCode != null;

            if (!loginCode.equals(current)) {
                return new EditRes("비밀번호가 틀립니다.\n임시 로그인 중이라면 로그인 코드를 입력해주세요.");
            }
        }

        user.setPassword(password);
        user.encryptPassword(passwordEncoder);
        save(user);

        return new EditRes();
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

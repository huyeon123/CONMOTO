package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.huyeon.apiserver.support.JsonParse.readJson;
import static com.huyeon.apiserver.support.JsonParse.writeJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //회원가입
    public boolean signUp(String signUpForm) {
        User user = readJson(signUpForm, User.class);
        if(user == null) return false;
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    //회원정보
    public String userInfo(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return writeJson(user);
        }
        return null;
    }

    //회원정보 수정
    public boolean editInfo(Long id, String editForm) {
        Optional<User> optionalUser = userRepository.findById(id);
        User currentUser = optionalUser.orElse(new User());

        User editUser = readJson(editForm, User.class);

        //요청보낸 사용자와 수정정보 ID가 일치할때만 반영 => Security 작업할 때 보완
        if (editUser != null
                && editUser.getId().equals(currentUser.getId())) {
            userRepository.save(editUser);
            return true;
        }
        return false;
    }

    //회원탈퇴
    public boolean resign(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //게시글 확인

    //댓글 확인

    //회원정보 수정이력 확인

    //댓글 수정이력 확인

    //게시글 수정이력 확인
}

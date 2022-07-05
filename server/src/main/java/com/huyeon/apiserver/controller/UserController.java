package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.dto.UserSignupRequestDto;
import com.huyeon.apiserver.model.dto.base.ResponseDto;
import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.huyeon.apiserver.support.JsonParse.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@RequestBody UserSignupRequestDto request) {
        ResponseDto response = new ResponseDto();
        if(userService.signUp(request)){
            log.info("회원 가입 완료");
            response.setMessage("회원 가입에 성공했습니다.");
            response.setSuccess(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setMessage("회원 가입에 실패했습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //로그인(Security로 대체 예정, 테스트용)
    @PostMapping("/signin")
    public ResponseEntity<ResponseDto> signIn(@ModelAttribute User user) {
        ResponseDto response = new ResponseDto();
        if(userService.signIn(user)){
            log.info("로그인 완료");
            response.setMessage("로그인에 성공했습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setMessage("로그인에 실패했습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //회원정보
    @GetMapping("/{id}/profile")
    public String userInfo(@PathVariable Long id) {
        User user = userService.userInfo(id);
        if(user.getId() == null) return "회원 정보가 존재하지 않습니다.";
        return writeJson(user);
    }

    //회원정보 수정
    @PostMapping("/{id}/edit")
    public String editInfo(@PathVariable Long id, @RequestBody String editForm) {
        if(userService.editInfo(id, editForm)) {
            return "정보가 정상적으로 반영되었습니다.";
        }
        return "회원정보 수정에 실패했습니다.";
    }

    //회원탈퇴
    @DeleteMapping("/{id}")
    public String resign(@PathVariable Long id) {
        if(userService.resign(id)) return "이용해주셔서 감사합니다.";
        return "회원 탈퇴에 실패했습니다";
    }
}

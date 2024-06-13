package com.ppyongppyong.server.user.controller;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.user.dto.UserLoginRequestDto;
import com.ppyongppyong.server.user.dto.UserLoginResponseDto;
import com.ppyongppyong.server.user.dto.UserSignupRequestDto;
import com.ppyongppyong.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "[회원가입] api")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserSignupRequestDto userSignupRequestDto) {
        return ResponseEntity.ok(userService.signup(userSignupRequestDto));
    }

    @Operation(summary = "일반 로그인", description = "[일반 로그인] api")
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto userLoginRequestDto,
                                                      HttpServletResponse response) throws IOException {
        return ResponseEntity.ok(userService.login(userLoginRequestDto, response));
    }

    //Todo: Redis 적용 후 "[로그아웃] RefreshToken 제거 및 쿠키 삭제" api 추가 예정
    @Operation(summary = "Accress Token 재발급", description = "[AccressToken 만료 시 RefreshToken을 재발급] api / RefreshToken 필요")
    @PostMapping("/reissue")
    public ResponseEntity<String> reIssueAccessToken(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        return ResponseEntity.ok(userService.reIssueAccessToken(userDetails.getUser(), httpServletResponse));
    }

}
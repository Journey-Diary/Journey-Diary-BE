package com.ppyongppyong.server.user.dto;

import com.ppyongppyong.server.user.entity.CustomEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSignupRequestDto {
    @CustomEmail(message = "이메일 형식으로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "(?=.*?[A-Z])(?=.*?[\\d])(?=.*?[~!@#$%^&*()_+=\\-`]).{8,15}",
            message = "비밀번호는 영문 대문자, 특수기호, 숫자가 적어도 1개 이상씩 포함된 조합이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String userName;

    @Builder
    public UserSignupRequestDto(String userId, String password, String passwordCheck, String userName) {
        this.userId = userId;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.userName = userName;
    }
}

package com.ppyongppyong.server.user.dto;

import com.ppyongppyong.server.user.entity.CustomEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    @CustomEmail(message = "이메일 형식으로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder
    public UserLoginRequestDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}

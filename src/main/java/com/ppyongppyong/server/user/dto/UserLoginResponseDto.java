package com.ppyongppyong.server.user.dto;

import com.ppyongppyong.server.user.entity.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
public class UserLoginResponseDto {
    @Comment("[DB] id")
    private Long id;

    @Comment("[이메일] id")
    private String userId;

    @Comment("로그인 여부")
    private boolean isActive;

    @Comment("프로필 이미지")
    private String userProfileImage;

    @Comment("프로필 배경 이미지")
    private String userBackgroundImage;

    @Comment("자기소개")
    private String introduce;

    @Comment("일반/관리자")
    private UserRoleEnum userRole;

    @Comment("AccessToken")
    private String accessToken;

    @Comment("RefreshToken")
    private String refreshToken;

    @Builder
    public UserLoginResponseDto(Long id, String userId, boolean isActive, String userProfileImage, String userBackgroundImage, String introduce, UserRoleEnum userRole, String accessToken, String refreshToken) {
        this.id = id;
        this.userId = userId;
        this.isActive = isActive;
        this.userProfileImage = userProfileImage;
        this.userBackgroundImage = userBackgroundImage;
        this.introduce = introduce;
        this.userRole = userRole;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

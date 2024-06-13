package com.ppyongppyong.server.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CustomEmail// @가 없거나 영문이 아닌 한글인 경우, 특수기호는 오류
    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = true)
    private String userProfileImage;

    @Column(nullable = true)
    private String userBackgroundImage;

    @Column(nullable = true)
    private String introduce;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String naverId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    @Builder
    public User(Long id, String userId, String password, String userName, boolean isActive, String userProfileImage, String userBackgroundImage, String introduce, Long kakaoId, String naverId, UserRoleEnum userRole) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.isActive = isActive;
        this.userProfileImage = userProfileImage;
        this.userBackgroundImage = userBackgroundImage;
        this.introduce = introduce;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
        this.userRole = userRole;
    }

    public void updateIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}

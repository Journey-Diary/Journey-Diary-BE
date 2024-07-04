package com.ppyongppyong.server.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDataDto {

    private Long id;
    private String userId;
    private String userName;

    @Builder
    public UserDataDto(Long id, String userId, String userName){
        this.id = id;
        this.userId = userId;
        this.userName = userName;
    }
}

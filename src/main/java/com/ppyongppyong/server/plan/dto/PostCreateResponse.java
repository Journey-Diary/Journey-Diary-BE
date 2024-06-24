package com.ppyongppyong.server.plan.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreateResponse {
    private Long id;


    @Builder
    public PostCreateResponse(Long id){
        this.id = id;
    }
}


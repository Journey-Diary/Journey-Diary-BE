package com.ppyongppyong.server.plan.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PlanDataResponseDto {
    private Long planDataId;
    private String mbti;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Boolean isOpen;
    private Long hit;
    private Boolean isShare;
    private Long planId;
    private List<PostResponseDto> posts;

    @Builder
    public PlanDataResponseDto(Long planDataId, String mbti, String title, LocalDate startDate, LocalDate endDate, String location,
                               Boolean isOpen, Long hit, Boolean isShare, Long planId, List<PostResponseDto> posts) {
        this.planDataId = planDataId;
        this.mbti = mbti;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.isOpen = isOpen;
        this.hit = hit;
        this.isShare = isShare;
        this.planId = planId;
        this.posts = posts;
    }
}

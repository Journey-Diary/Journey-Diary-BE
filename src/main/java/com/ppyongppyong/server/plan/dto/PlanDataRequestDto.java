package com.ppyongppyong.server.plan.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PlanDataRequestDto {
    private String title;
    private LocalDate startedDate;
    private LocalDate endedDate;
    private String location;
    private Boolean isOpen;
    private Boolean isShare;
}

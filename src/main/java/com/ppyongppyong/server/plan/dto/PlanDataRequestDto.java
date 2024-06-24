package com.ppyongppyong.server.plan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanDataRequestDto {
    private String title;
    private String startDate;
    private String endDate;
    private String location;
    private Boolean isOpen;
    private Boolean isShare;
}

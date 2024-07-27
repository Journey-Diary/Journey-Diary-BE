package com.ppyongppyong.server.plan.dto;

import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.plan.entity.PlanData;
import com.ppyongppyong.server.user.dto.UserDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanDataMapper {

    PlanDataMapper INSTANCE = Mappers.getMapper(PlanDataMapper.class);

    @Mapping(target = "mbti", source = "plan.mbti")
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planDataId", source ="planData.id")
    PlanDataResponseDto planDataToPlanResponse(PlanData planData, Plan plan, List<UserDataDto> users);

}

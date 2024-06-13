package com.ppyongppyong.server.plan.dto;

import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.user.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);

    @Mapping(target = "group", source = "group")
    Plan planPorJCreateRequestDtoToEntity(PlanPorJCreateRequest dto, Group group);

    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "mbti", source = "plan.mbti")
    PlanPorJCreateResponse entityToPlanProJResponseDto(Plan plan);
}

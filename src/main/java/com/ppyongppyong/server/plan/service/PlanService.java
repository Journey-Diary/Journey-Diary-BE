package com.ppyongppyong.server.plan.service;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.common.exception.CustomException;
import com.ppyongppyong.server.plan.dto.PlanMapper;
import com.ppyongppyong.server.plan.dto.PlanPorJCreateRequest;
import com.ppyongppyong.server.plan.dto.PlanPorJCreateResponse;
import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.plan.repository.PlanRepository;
import com.ppyongppyong.server.user.entity.Group;
import com.ppyongppyong.server.user.entity.GroupTypeEnum;
import com.ppyongppyong.server.user.entity.User;
import com.ppyongppyong.server.user.entity.UserGroupConnect;
import com.ppyongppyong.server.user.repository.GroupRepository;
import com.ppyongppyong.server.user.repository.UserGroupConnectRepository;
import com.ppyongppyong.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ppyongppyong.server.common.exception.massage.ErrorMsg.NOT_FOUND_ACCOUNT;
import static com.ppyongppyong.server.user.entity.UserGroupRoleEnum.CREATOR;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;
    private final PlanMapper planMapper;
    private final GroupRepository groupRepository;
    private final UserGroupConnectRepository userGroupConnectRepository;
    private final PlanRepository planRepository;

    @Transactional
    public PlanPorJCreateResponse createPlanPorJ(PlanPorJCreateRequest planPorJCreateRequest, UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(NOT_FOUND_ACCOUNT)
        );

        Group group = Group.builder()
                .groupType(GroupTypeEnum.INDIVIDUAL)
                .groupName(user.getUserName())
                .build();

        UserGroupConnect userGroupConnect = UserGroupConnect.builder()
                .user(user)
                .group(group)
                .isInvited(true)
                .userGroupRoleEnum(CREATOR)
                .build();

        groupRepository.save(group);
        userGroupConnectRepository.save(userGroupConnect);

        Plan plan = planMapper.planPorJCreateRequestDtoToEntity(planPorJCreateRequest, group);
        planRepository.save(plan);

        return planMapper.entityToPlanProJResponseDto(plan);
    }
}

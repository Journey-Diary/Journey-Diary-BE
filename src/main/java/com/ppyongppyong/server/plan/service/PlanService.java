package com.ppyongppyong.server.plan.service;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.common.exception.CustomException;
import com.ppyongppyong.server.plan.dto.*;
import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.plan.entity.PlanData;
import com.ppyongppyong.server.plan.entity.Post;
import com.ppyongppyong.server.plan.repository.PlanDataRepository;
import com.ppyongppyong.server.plan.repository.PlanRepository;
import com.ppyongppyong.server.plan.repository.PostRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.ppyongppyong.server.common.exception.massage.ErrorMsg.*;
import static com.ppyongppyong.server.user.entity.UserGroupRoleEnum.CREATOR;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;
    private final PlanMapper planMapper;
    private final GroupRepository groupRepository;
    private final UserGroupConnectRepository userGroupConnectRepository;
    private final PlanRepository planRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PlanDataMapper planDataMapper;
    private final PlanDataRepository planDataRepository;

    @Transactional
    public PlanPorJCreateResponse createPlanPorJ(PlanPorJCreateRequest planPorJCreateRequest, UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(NOT_FOUND_ACCOUNT));

        Group group = Group.builder().groupType(GroupTypeEnum.INDIVIDUAL).groupName(user.getUserName()).build();

        UserGroupConnect userGroupConnect = UserGroupConnect.builder().user(user).group(group).isInvited(true).userGroupRoleEnum(CREATOR).build();

        groupRepository.save(group);
        userGroupConnectRepository.save(userGroupConnect);

        Plan plan = planMapper.planPorJCreateRequestDtoToEntity(planPorJCreateRequest, group);
        planRepository.save(plan);

        return planMapper.entityToPlanProJResponseDto(plan);
    }

    public PlanDataResponseDto getPlan(Long planId, UserDetailsImpl userDetails) {

        // 유저 인증
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(NOT_FOUND_ACCOUNT));

        // 플랜 조회
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(NOT_FOUND_PLAN));

        // 플랜 세부 정보 조회
        PlanData planData = planDataRepository.findByPlan(plan).orElse(null);

        // 조회 가능한 상태인지 체크
        List<UserGroupConnect> connects = userGroupConnectRepository.findByGroup(plan.getGroup());
        List<Long> userIds = connects.stream().map(c -> c.getUser().getId()).collect(Collectors.toList());

        // 플랜 그룹원이 아닌 경우
        if(!userIds.contains(user.getId())){
            if (planData == null)
                new CustomException(NOT_FOUND_PLAN);
            // 여행일이 끝나지 않았거나 공개가 아닌 경우 조회 불가
            if(!LocalDate.now().isAfter(planData.getEndedDate()) || !planData.getIsOpen())
                new CustomException(CANNOT_ACCESS_PLAN);
        }

        // 포스트 매핑
        List<Post> posts = postRepository.findByPlan(plan);
        List<PostResponseDto> postResponseDtos = posts.stream().map(p -> {
            return postMapper.postToPostResponse(p, p.getDate().getOrderIndex());
        }).collect(Collectors.toList());

        return planDataMapper.planDataToPlanResponse(planData, plan);

    }
}

package com.ppyongppyong.server.plan.service;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.common.exception.CustomException;
import com.ppyongppyong.server.plan.dto.*;
import com.ppyongppyong.server.plan.entity.Date;
import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.plan.entity.PlanData;
import com.ppyongppyong.server.plan.entity.Post;
import com.ppyongppyong.server.plan.repository.DateRepository;
import com.ppyongppyong.server.plan.repository.PlanDataRepository;
import com.ppyongppyong.server.plan.repository.PlanRepository;
import com.ppyongppyong.server.plan.repository.PostRepository;
import com.ppyongppyong.server.user.dto.UserDataDto;
import com.ppyongppyong.server.user.dto.UserMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final DateRepository dateRepository;
    private final UserMapper userMapper;

    @Transactional
    public PlanPorJCreateResponse createPlanPorJ(PlanPorJCreateRequest planPorJCreateRequest, UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

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
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 플랜 조회
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(NOT_FOUND_PLAN));

        // 플랜 세부 정보 조회
        PlanData planData = planDataRepository.findByPlan(plan).orElse(null);

        // 조회 가능한 상태인지 체크
        List<UserGroupConnect> connects = userGroupConnectRepository.findByGroup(plan.getGroup());
        List<Long> userIds = connects.stream().map(c -> c.getUser().getId()).collect(Collectors.toList());

        // 플랜 그룹원이 아닌 경우
        if (!userIds.contains(user.getId())) {
            // 작성되지 않은 플랜 조회 불가
            if (planData == null)
                throw new CustomException(NOT_FOUND_PLAN);
            // 여행일이 끝나지 않았거나 공개가 아닌 경우 조회 불가
            if (!LocalDate.now().isAfter(planData.getEndDate()) || !planData.getIsOpen())
                throw new CustomException(CANNOT_ACCESS_PLAN);
            planData.increaseHit();
            planDataRepository.save(planData);
        }
        List<UserDataDto> userDtos = userRepository.findAllById(userIds).stream().map(u -> userMapper.entityToUserDataDto(u))
                .collect(Collectors.toList());
        return planDataMapper.planDataToPlanResponse(planData, plan, userDtos);
    }

    @Transactional
    public PostCreateResponse createPost(Long planId, String dateStr, UserDetailsImpl userDetails) {

        // 유저 인증
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 플랜 조회
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(NOT_FOUND_PLAN));

        // 플랜 세부 정보 조회
        PlanData planData = planDataRepository.findByPlan(plan).orElse(null);

        if (planData.getStartDate() == null || planData.getEndDate() == null)
            throw new CustomException(CANNOT_CREATE_POST);

        // 팀장인지 확인
        validateLeader(user, plan);

        // date가 해당 plan에 유효한지 확인
        int startDate = localDateToInteger(planData.getStartDate());
        int endDate = localDateToInteger(planData.getEndDate());
        int date = Integer.parseInt(dateStr);
        if (date < startDate || date > endDate)
            throw new CustomException(INVALID_POST_DATE);
        int index = date - startDate + 1;

        // 플랜의 해당 날짜 Date
        Date postDate = dateRepository.findByPlan(plan).stream().filter(d -> isSameDate(d, plan, index)).findFirst().get();

        //포스트 생성, 저장
        Post post = Post.builder().date(postDate).build();
        postRepository.save(post);

        //포스트 아이디 반환
        return PostCreateResponse.builder().id(post.getId()).build();
    }

    private void validateLeader(User user, Plan plan) {
        // 팀장 정보 조회
        User leader = userGroupConnectRepository.findByGroup(plan.getGroup()).stream()
                .filter(UserGroupConnect::isLeader).findFirst().get().getUser();

        //유저가 팀장인지 확인
        if (user != leader)
            throw new CustomException(CANNOT_UPDATE_POST);
    }

    private boolean isSameDate(Date date, Plan plan, int index) {
        return date.getPlan() == plan && date.getOrderIndex() == index;
    }

    /**
     * 플랜 업데이트 (Patch)
     *
     * @param planId
     * @param requestDto
     * @param userDetails
     * @return
     */
    @Transactional
    public PlanDataResponseDto updatePlan(Long planId, PlanDataRequestDto requestDto, UserDetailsImpl userDetails) {

        // 유저 인증
        User user = userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 플랜 조회
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(NOT_FOUND_PLAN));

        // 플랜 세부 정보 조회
        PlanData planData2 = planDataRepository.findByPlan(plan).orElse(null);
        PlanData planData = planData2;
        if (planData == null) {
            planData = PlanData.builder().build();
            planData.setPlan(plan);
        }

        // 팀장인지 확인
        validateLeader(user, plan);

        if (requestDto.getTitle() != null)
            planData.setTitle(requestDto.getTitle());

        // 최초 여행 날짜 설정 시, 시작 종료일 둘다 입력해야 함
        if (planData.getStartDate() == null && planData.getEndDate() == null) {
            // 둘중에 하나만 입력한 경우 에러
            if ((requestDto.getStartDate() == null) != (requestDto.getEndDate() == null))
                throw new CustomException(INVALID_PLAN_DATE);

            // 임시에러, 날짜는 수정 불가, 최초 생성시에만 설정 가능 (추후 변경해야 함)
        } else if (requestDto.getStartDate() != null || requestDto.getEndDate() != null)
            throw new IllegalStateException();


//        if (requestDto.getStartDate() != null)
//            planData.setStartDate(stringToLocalDate(requestDto.getStartDate()));
//        if (requestDto.getEndDate() != null)
//            planData.setEndDate(stringToLocalDate(requestDto.getEndDate()));
        // 시작날짜, 종료날짜 유효성 체크
        planData.validDate();

        if (planData2 != null && planData2.existDate()) {
//            List<Date> dates = dateRepository.findByPlan(plan);
//            dateRepository.deleteAll(dates); 삭제하면 안됨 post에서 date를 들고있음
        } else {
            // 저장된 planData가 없거나 최초 날짜 설정이라면
            int days = localDateToInteger(planData.getEndDate()) - localDateToInteger(planData.getStartDate());
            List<Date> dates = IntStream.range(1, days + 1)
                    .mapToObj(i -> Date.builder().orderIndex(i).plan(plan).build()).collect(Collectors.toList());
            dateRepository.saveAll(dates);
        }

        if (requestDto.getLocation() != null)
            planData.setLocation(requestDto.getLocation());
        if (requestDto.getIsOpen() != null)
            planData.setIsOpen(requestDto.getIsOpen());
        if (requestDto.getIsShare() != null)
            planData.setIsShare(requestDto.getIsShare());

        planDataRepository.save(planData);

        List<Long> userIds = userGroupConnectRepository.findByGroup(plan.getGroup()).stream()
                .map(c -> c.getUser().getId()).collect(Collectors.toList());

        List<UserDataDto> userDtos = userRepository.findAllById(userIds).stream()
                .map(u -> userMapper.entityToUserDataDto(u)).collect(Collectors.toList());
        return planDataMapper.planDataToPlanResponse(planData, plan, userDtos);
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private LocalDate stringToLocalDate(String date) {
        return LocalDate.parse(date, formatter);
    }

    private int localDateToInteger(LocalDate date) {
        return Integer.parseInt(date.format(formatter));
    }
}

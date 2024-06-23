package com.ppyongppyong.server.plan.controller;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.plan.dto.PlanDataRequestDto;
import com.ppyongppyong.server.plan.dto.PlanPorJCreateRequest;
import com.ppyongppyong.server.plan.dto.PlanPorJCreateResponse;
import com.ppyongppyong.server.plan.dto.PlanDataResponseDto;
import com.ppyongppyong.server.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    @Operation(summary = "plan P/J 선택", description = "[plan P/J 선택] api")
    @PostMapping("/mbti")
    public ResponseEntity<PlanPorJCreateResponse> createPlanPorJ(@RequestBody PlanPorJCreateRequest planPorJCreateRequest,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(planService.createPlanPorJ(planPorJCreateRequest, userDetails));
    }

    @Operation(summary = "plan 조회")
    @GetMapping("/{planId}")
    public ResponseEntity<PlanDataResponseDto> getPlan(@PathVariable Long planId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(planService.getPlan(planId, userDetails));
    }

    @Operation(summary = "plan 날짜별 post 생성")
    @PostMapping("/{planId}")
    public ResponseEntity<Long> createPost(@PathVariable Long planId, @RequestParam("date") String date,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(planService.createPost(planId, date, userDetails));
    }


    @Operation(summary = "plan 수정")
    @PatchMapping("/{planId}")
    public ResponseEntity<PlanDataResponseDto> updatePlan(@PathVariable Long planId, @RequestBody PlanDataRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(planService.updatePlan(planId, requestDto, userDetails));
    }


}

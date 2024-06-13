package com.ppyongppyong.server.plan.controller;

import com.ppyongppyong.server.common.UserDetailsImpl;
import com.ppyongppyong.server.plan.dto.PlanPorJCreateRequest;
import com.ppyongppyong.server.plan.dto.PlanPorJCreateResponse;
import com.ppyongppyong.server.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

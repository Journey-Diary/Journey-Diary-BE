package com.ppyongppyong.server.plan.repository;

import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.plan.entity.PlanData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PlanDataRepository extends JpaRepository<PlanData, Long> {
    Optional<PlanData> findByPlan(Plan plan);
}

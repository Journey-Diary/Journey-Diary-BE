package com.ppyongppyong.server.plan.repository;

import com.ppyongppyong.server.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}

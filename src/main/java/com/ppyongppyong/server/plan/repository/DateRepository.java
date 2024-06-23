package com.ppyongppyong.server.plan.repository;

import com.ppyongppyong.server.plan.entity.Date;
import com.ppyongppyong.server.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DateRepository extends JpaRepository<Date, Long> {
    List<Date> findByPlan(Plan plan);
}

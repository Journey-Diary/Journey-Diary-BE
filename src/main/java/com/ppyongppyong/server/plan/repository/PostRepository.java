package com.ppyongppyong.server.plan.repository;

import com.ppyongppyong.server.plan.entity.Plan;
import com.ppyongppyong.server.plan.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findPostById(Long postId);
    List<Post> findByPlan(Plan plan);
}

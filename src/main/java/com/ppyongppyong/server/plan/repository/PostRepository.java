package com.ppyongppyong.server.plan.repository;

import com.ppyongppyong.server.plan.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findPostById(Long postId);
}

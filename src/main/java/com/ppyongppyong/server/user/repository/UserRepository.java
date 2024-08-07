package com.ppyongppyong.server.user.repository;

import com.ppyongppyong.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserId(String userId);

    List<User> findByUserIdContaining(String str);
}

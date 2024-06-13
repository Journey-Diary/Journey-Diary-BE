package com.ppyongppyong.server.user.repository;

import com.ppyongppyong.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //    Optional<Account> findByAccount(String account);

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserId(String userId);
//    Optional<Account> findById(Long id);
}

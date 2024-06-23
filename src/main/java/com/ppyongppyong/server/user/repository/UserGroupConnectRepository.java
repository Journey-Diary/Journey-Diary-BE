package com.ppyongppyong.server.user.repository;

import com.ppyongppyong.server.user.entity.Group;
import com.ppyongppyong.server.user.entity.UserGroupConnect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupConnectRepository extends JpaRepository<UserGroupConnect, Long> {
    List<UserGroupConnect> findByGroup(Group group);
}

package com.ppyongppyong.server.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "userGroupConnect")
public class UserGroupConnect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false)
    private Boolean isInvited;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserGroupRoleEnum userGroupRoleEnum;

    @Builder
    public UserGroupConnect(Long id, User user, Group group, Boolean isInvited, UserGroupRoleEnum userGroupRoleEnum) {
        this.id = id;
        this.user = user;
        this.group = group;
        this.isInvited = isInvited;
        this.userGroupRoleEnum = userGroupRoleEnum;
    }

    public boolean isLeader(){
        return UserGroupRoleEnum.CREATOR == this.userGroupRoleEnum;
    }
}

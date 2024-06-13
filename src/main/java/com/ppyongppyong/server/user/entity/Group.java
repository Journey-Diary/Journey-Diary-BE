package com.ppyongppyong.server.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private GroupTypeEnum groupType;

    @Column(nullable = false)
    private String groupName;

    @Builder
    public Group(Long id, GroupTypeEnum groupType, String groupName) {
        this.id = id;
        this.groupType = groupType;
        String newName;
        if (groupType == GroupTypeEnum.TEAM) {
            newName = groupName + "의 팀";
        } else {
            newName = groupName;
        }
        this.groupName = newName;
    }
}

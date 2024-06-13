package com.ppyongppyong.server.plan.entity;

import com.ppyongppyong.server.user.entity.Group;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column
    @Enumerated(EnumType.STRING)
    private PlanMbtiEnum mbti;

    @Builder
    public Plan(Group group, PlanMbtiEnum mbti) {
        this.group = group;
        this.mbti = mbti;
    }
}

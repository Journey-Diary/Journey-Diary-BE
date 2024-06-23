package com.ppyongppyong.server.plan.entity;

import com.ppyongppyong.server.common.entity.BaseDomain;
import com.ppyongppyong.server.common.entity.BaseDomainWithId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "date")
public class Date extends BaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "date", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("date asc")
    private Set<Post> posts = new LinkedHashSet<>();

    @Builder
    public Date(Integer orderIndex, Plan plan, Set<Post> posts) {
        this.orderIndex = orderIndex;
        this.plan = plan;
        this.posts = posts;
    }

}

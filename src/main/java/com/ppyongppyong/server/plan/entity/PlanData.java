package com.ppyongppyong.server.plan.entity;

import com.ppyongppyong.server.common.entity.BaseDomain;
import com.ppyongppyong.server.common.exception.CustomException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.ppyongppyong.server.common.exception.massage.ErrorMsg.INVALID_PLAN_DATE;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "planData")
public class PlanData extends BaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @Comment("제목")
    private String title;

    @Column(name = "start_date")
    @Comment("시작일")
    private LocalDate startDate;

    @Column(name = "end_date")
    @Comment("종료일")
    private LocalDate endDate;

    @Column(name = "location", columnDefinition = "TEXT")
    @Comment("위치")
    private String location;

    @Column(name = "is_open")
    @Comment("노출 여부")
    private Boolean isOpen = true;

    @Column(name = "hit")
    @Comment("조회수")
    private long hit;

    @Column(name = "is_share")
    @Comment("공유 여부")
    private Boolean isShare = true;

    @OneToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany
    @OrderBy("orderIndex asc")
    private Set<Date> dates = new LinkedHashSet<>();

    @Builder
    public PlanData(String title, LocalDate startDate, LocalDate endDate, String location, Boolean isOpen, long hit, Boolean isShare, Plan plan, Set<Date> dates) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.isOpen = isOpen;
        this.hit = hit;
        this.isShare = isShare;
        this.plan = plan;
        this.dates = dates;
    }

    public void validDate() {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) throw new CustomException(INVALID_PLAN_DATE);
        }
    }

    public void increaseHit() {
        hit++;
    }

    public boolean existDate(){
        return startDate != null && endDate != null;
    }

}

package com.ppyongppyong.server.plan.entity;

import com.ppyongppyong.server.common.entity.BaseDomain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "post")
@Setter
public class Post extends BaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @Comment("제목")
    private String title;

    @Column(name = "location", columnDefinition = "TEXT")
    @Comment("위치")
    private String location;

    @Column(name = "x")
    @Comment("x 좌표")
    private String x;

    @Column(name = "y")
    @Comment("y 좌표")
    private String y;

    @Column(name = "phone")
    @Comment("전화번호")
    private String phone;

    @Column(name = "time")
    @Comment("시간")
    private String time;

    @ManyToOne
    @JoinColumn(name = "date_id")
    @Comment("일자 외래키")
    private Date date;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @Comment("플랜")
    private Plan plan;

    @Builder
    public Post(String title, String content, String location, String x, String y, String phone, String time,
                LocalDateTime endAt, Integer orderIndex, Date date, String memo) {
        this.title = title;
        this.location = location;
        this.x = x;
        this.y = y;
        this.phone = phone;
        this.time = time;
        this.date = date;
        this.memo = memo;
    }
}

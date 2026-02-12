package com.worldcup.dealfinderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_deal_summary", schema = "deal_finder_schema",
        uniqueConstraints = @UniqueConstraint(
            name = "uq_summary_match_cat",
            columnNames = {"match_id", "category"}
        ),
        indexes = {
                @Index(name = "idx_summary_match", columnList = "match_id"),
                @Index(name = "idx_summary_lowest", columnList = "lowest_price")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDealSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "lowest_price", precision = 10, scale = 2)
    private BigDecimal lowestPrice;

    @Column(name = "highest_price", precision = 10, scale = 2)
    private BigDecimal highestPrice;

    @Column(name = "average_price", precision = 10, scale = 2)
    private BigDecimal averagePrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "best_provider_id")
    private Provider bestProvider;

    @Column(name = "best_deal_score")
    private Integer bestDealScore;

    @Column(name = "num_providers")
    private Integer numProviders;

    @Column(name = "overall_trend", length = 20)
    private String overallTrend;

    @Column(name = "best_time_to_buy", length = 30)
    private String bestTimeToBuy;

    @Column(name = "last_computed_at", nullable = false)
    private LocalDateTime lastComputedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

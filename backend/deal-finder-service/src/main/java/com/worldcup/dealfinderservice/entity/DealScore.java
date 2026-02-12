package com.worldcup.dealfinderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deal_scores", schema = "deal_finder_schema",
        uniqueConstraints = @UniqueConstraint(
            name = "uq_deal_match_provider_cat",
            columnNames = {"match_id", "provider_id", "category"}
        ),
        indexes = {
                @Index(name = "idx_deals_match", columnList = "match_id"),
                @Index(name = "idx_deals_score", columnList = "deal_score"),
                @Index(name = "idx_deals_match_cat", columnList = "match_id, category"),
                @Index(name = "idx_deals_trend", columnList = "price_trend")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "deal_score", nullable = false)
    private Integer dealScore;

    @Column(name = "current_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "market_average", nullable = false, precision = 10, scale = 2)
    private BigDecimal marketAverage;

    @Column(name = "savings_percentage", precision = 5, scale = 2)
    private BigDecimal savingsPercentage;

    @Column(name = "price_trend", length = 20)
    private String priceTrend;

    @Column(name = "trend_percentage", precision = 5, scale = 2)
    private BigDecimal trendPercentage;

    @Column(name = "price_7d_low", precision = 10, scale = 2)
    private BigDecimal price7dLow;

    @Column(name = "price_7d_high", precision = 10, scale = 2)
    private BigDecimal price7dHigh;

    @Column(name = "best_time_to_buy", length = 30)
    private String bestTimeToBuy;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "booking_url", nullable = false, columnDefinition = "TEXT")
    private String bookingUrl;

    @Column(name = "last_computed_at", nullable = false)
    private LocalDateTime lastComputedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

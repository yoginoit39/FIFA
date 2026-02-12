package com.worldcup.dealfinderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_snapshots", schema = "deal_finder_schema",
        indexes = {
                @Index(name = "idx_snapshots_match", columnList = "match_id"),
                @Index(name = "idx_snapshots_provider", columnList = "provider_id"),
                @Index(name = "idx_snapshots_match_cat", columnList = "match_id, category"),
                @Index(name = "idx_snapshots_match_provider", columnList = "match_id, provider_id"),
                @Index(name = "idx_snapshots_fetched", columnList = "fetched_at"),
                @Index(name = "idx_snapshots_total_price", columnList = "total_price")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceSnapshot {
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

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "fee_amount", precision = 10, scale = 2)
    private BigDecimal feeAmount;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "availability_status", length = 30)
    private String availabilityStatus;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "booking_url", nullable = false, columnDefinition = "TEXT")
    private String bookingUrl;

    @Column(name = "source_type", nullable = false, length = 20)
    private String sourceType;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

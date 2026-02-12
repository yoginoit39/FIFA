package com.worldcup.dealfinderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fetch_log", schema = "deal_finder_schema",
        indexes = {
                @Index(name = "idx_fetch_log_provider", columnList = "provider_id"),
                @Index(name = "idx_fetch_log_started", columnList = "started_at")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FetchLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @Column(name = "fetch_type", nullable = false, length = 30)
    private String fetchType;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "records_fetched")
    private Integer recordsFetched;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

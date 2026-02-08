package com.worldcup.matchservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Match Entity - Represents a FIFA World Cup 2026 match
 *
 * Stored in: match_service_schema.matches
 */
@Entity
@Table(name = "matches", schema = "match_service_schema",
        indexes = {
                @Index(name = "idx_match_date", columnList = "match_date"),
                @Index(name = "idx_match_status", columnList = "status"),
                @Index(name = "idx_match_stadium", columnList = "stadium_id"),
                @Index(name = "idx_match_round", columnList = "round")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_api_id", unique = true, length = 50)
    private String externalApiId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_team_id", referencedColumnName = "id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_team_id", referencedColumnName = "id")
    private Team awayTeam;

    @Column(name = "stadium_id")
    private Long stadiumId;  // Loose coupling to stadium service

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "match_time")
    private LocalTime matchTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Column(name = "home_score")
    private Integer homeScore = 0;

    @Column(name = "away_score")
    private Integer awayScore = 0;

    @Column(name = "round", length = 50)
    private String round;  // Group Stage, Round of 16, Quarter-final, etc.

    @Column(name = "group_name", length = 10)
    private String groupName;  // Group A, B, C, etc.

    @Column(name = "venue_name", length = 200)
    private String venueName;

    @Column(name = "venue_city", length = 100)
    private String venueCity;

    @Column(name = "venue_country", length = 100)
    private String venueCountry;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Match Status Enum
     */
    public enum MatchStatus {
        SCHEDULED,
        LIVE,
        FINISHED,
        POSTPONED,
        CANCELLED
    }
}

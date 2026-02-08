package com.worldcup.matchservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Team Entity - Represents a national team in FIFA World Cup 2026
 *
 * Stored in: match_service_schema.teams
 */
@Entity
@Table(name = "teams", schema = "match_service_schema",
        indexes = {
                @Index(name = "idx_team_external_id", columnList = "external_api_id"),
                @Index(name = "idx_team_country", columnList = "country")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_api_id", unique = true, length = 50)
    private String externalApiId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "fifa_ranking")
    private Integer fifaRanking;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

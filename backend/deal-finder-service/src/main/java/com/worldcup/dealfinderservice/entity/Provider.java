package com.worldcup.dealfinderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "providers", schema = "deal_finder_schema",
        indexes = {
                @Index(name = "idx_providers_name", columnList = "name"),
                @Index(name = "idx_providers_active", columnList = "is_active")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "website_url", nullable = false, length = 500)
    private String websiteUrl;

    @Column(name = "trust_score", nullable = false)
    private Integer trustScore;

    @Column(name = "fee_percentage", precision = 5, scale = 2)
    private BigDecimal feePercentage;

    @Column(name = "has_buyer_protection")
    private Boolean hasBuyerProtection;

    @Column(name = "api_type", nullable = false, length = 30)
    private String apiType;

    @Column(name = "api_base_url", length = 500)
    private String apiBaseUrl;

    @Column(name = "api_key_env_var", length = 100)
    private String apiKeyEnvVar;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "priority")
    private Integer priority;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

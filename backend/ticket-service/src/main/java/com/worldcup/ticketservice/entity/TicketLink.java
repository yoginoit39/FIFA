package com.worldcup.ticketservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * TicketLink Entity - Represents external ticket booking links
 *
 * Stored in: ticket_service_schema.ticket_links
 */
@Entity
@Table(name = "ticket_links", schema = "ticket_service_schema",
        indexes = {
                @Index(name = "idx_ticket_match", columnList = "match_id"),
                @Index(name = "idx_ticket_provider", columnList = "provider_name")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", nullable = false)
    private Long matchId;  // Reference to Match Service

    @Column(name = "provider_name", nullable = false, length = 100)
    private String providerName;  // FIFA, Ticketmaster, StubHub, SeatGeek

    @Column(name = "booking_url", nullable = false, columnDefinition = "TEXT")
    private String bookingUrl;

    @Column(name = "price_range", length = 100)
    private String priceRange;  // e.g., "$50 - $500"

    @Column(name = "min_price")
    private Integer minPrice;

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;  // AVAILABLE, SOLD_OUT, NOT_YET_AVAILABLE

    @Column(name = "priority")
    private Integer priority = 1;  // Display order (1 = highest)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

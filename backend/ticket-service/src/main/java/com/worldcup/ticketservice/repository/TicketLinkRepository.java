package com.worldcup.ticketservice.repository;

import com.worldcup.ticketservice.entity.TicketLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for TicketLink entity
 */
@Repository
public interface TicketLinkRepository extends JpaRepository<TicketLink, Long> {

    /**
     * Find ticket links by match ID, ordered by priority
     */
    List<TicketLink> findByMatchIdOrderByPriorityAsc(Long matchId);

    /**
     * Find ticket links by provider name
     */
    List<TicketLink> findByProviderNameOrderByPriorityAsc(String providerName);

    /**
     * Find ticket links by availability status
     */
    List<TicketLink> findByAvailabilityStatusOrderByPriorityAsc(String availabilityStatus);

    /**
     * Find all ticket links ordered by priority
     */
    @Query("SELECT t FROM TicketLink t ORDER BY t.priority ASC, t.providerName ASC")
    List<TicketLink> findAllOrderedByPriority();

    /**
     * Count ticket links by match
     */
    long countByMatchId(Long matchId);

    /**
     * Count ticket links by provider
     */
    long countByProviderName(String providerName);

    /**
     * Delete all ticket links for a match
     */
    void deleteByMatchId(Long matchId);
}

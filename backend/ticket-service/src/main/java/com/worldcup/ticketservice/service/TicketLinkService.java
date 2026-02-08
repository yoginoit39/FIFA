package com.worldcup.ticketservice.service;

import com.worldcup.ticketservice.dto.TicketLinkDTO;
import com.worldcup.ticketservice.entity.TicketLink;
import com.worldcup.ticketservice.exception.ResourceNotFoundException;
import com.worldcup.ticketservice.mapper.TicketLinkMapper;
import com.worldcup.ticketservice.repository.TicketLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for TicketLink operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketLinkService {

    private final TicketLinkRepository ticketLinkRepository;
    private final TicketLinkMapper ticketLinkMapper;

    /**
     * Get all ticket links
     */
    @Cacheable(value = "tickets", key = "'all'")
    public List<TicketLinkDTO> getAllTicketLinks() {
        log.debug("Fetching all ticket links");
        List<TicketLink> ticketLinks = ticketLinkRepository.findAllOrderedByPriority();
        return ticketLinkMapper.toTicketLinkDTOList(ticketLinks);
    }

    /**
     * Get ticket link by ID
     */
    @Cacheable(value = "tickets", key = "#id")
    public TicketLinkDTO getTicketLinkById(Long id) {
        log.debug("Fetching ticket link by ID: {}", id);
        TicketLink ticketLink = ticketLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket link not found with ID: " + id));
        return ticketLinkMapper.toTicketLinkDTO(ticketLink);
    }

    /**
     * Get ticket links by match ID
     */
    @Cacheable(value = "ticketsByMatch", key = "#matchId")
    public List<TicketLinkDTO> getTicketLinksByMatch(Long matchId) {
        log.debug("Fetching ticket links for match ID: {}", matchId);
        List<TicketLink> ticketLinks = ticketLinkRepository.findByMatchIdOrderByPriorityAsc(matchId);
        return ticketLinkMapper.toTicketLinkDTOList(ticketLinks);
    }

    /**
     * Get ticket links by provider
     */
    @Cacheable(value = "ticketsByProvider", key = "#providerName")
    public List<TicketLinkDTO> getTicketLinksByProvider(String providerName) {
        log.debug("Fetching ticket links by provider: {}", providerName);
        List<TicketLink> ticketLinks = ticketLinkRepository.findByProviderNameOrderByPriorityAsc(providerName);
        return ticketLinkMapper.toTicketLinkDTOList(ticketLinks);
    }

    /**
     * Get ticket links by availability status
     */
    public List<TicketLinkDTO> getTicketLinksByStatus(String status) {
        log.debug("Fetching ticket links by status: {}", status);
        List<TicketLink> ticketLinks = ticketLinkRepository.findByAvailabilityStatusOrderByPriorityAsc(status);
        return ticketLinkMapper.toTicketLinkDTOList(ticketLinks);
    }

    /**
     * Create new ticket link
     */
    @Transactional
    @CacheEvict(value = {"tickets", "ticketsByMatch", "ticketsByProvider"}, allEntries = true)
    public TicketLinkDTO createTicketLink(TicketLinkDTO ticketLinkDTO) {
        log.info("Creating new ticket link for match ID: {}", ticketLinkDTO.getMatchId());

        TicketLink ticketLink = ticketLinkMapper.toTicketLinkEntity(ticketLinkDTO);
        TicketLink savedTicketLink = ticketLinkRepository.save(ticketLink);
        log.info("Ticket link created successfully with ID: {}", savedTicketLink.getId());

        return ticketLinkMapper.toTicketLinkDTO(savedTicketLink);
    }

    /**
     * Update existing ticket link
     */
    @Transactional
    @CacheEvict(value = {"tickets", "ticketsByMatch", "ticketsByProvider"}, allEntries = true)
    public TicketLinkDTO updateTicketLink(Long id, TicketLinkDTO ticketLinkDTO) {
        log.info("Updating ticket link with ID: {}", id);

        TicketLink existingTicketLink = ticketLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket link not found with ID: " + id));

        ticketLinkMapper.updateTicketLinkFromDTO(ticketLinkDTO, existingTicketLink);
        TicketLink updatedTicketLink = ticketLinkRepository.save(existingTicketLink);
        log.info("Ticket link updated successfully with ID: {}", updatedTicketLink.getId());

        return ticketLinkMapper.toTicketLinkDTO(updatedTicketLink);
    }

    /**
     * Delete ticket link
     */
    @Transactional
    @CacheEvict(value = {"tickets", "ticketsByMatch", "ticketsByProvider"}, allEntries = true)
    public void deleteTicketLink(Long id) {
        log.info("Deleting ticket link with ID: {}", id);

        if (!ticketLinkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket link not found with ID: " + id);
        }

        ticketLinkRepository.deleteById(id);
        log.info("Ticket link deleted successfully with ID: {}", id);
    }

    /**
     * Delete all ticket links for a match
     */
    @Transactional
    @CacheEvict(value = {"tickets", "ticketsByMatch", "ticketsByProvider"}, allEntries = true)
    public void deleteTicketLinksByMatch(Long matchId) {
        log.info("Deleting all ticket links for match ID: {}", matchId);
        ticketLinkRepository.deleteByMatchId(matchId);
        log.info("All ticket links deleted for match ID: {}", matchId);
    }

    /**
     * Count total ticket links
     */
    public long countTicketLinks() {
        return ticketLinkRepository.count();
    }

    /**
     * Count ticket links by match
     */
    public long countTicketLinksByMatch(Long matchId) {
        return ticketLinkRepository.countByMatchId(matchId);
    }

    /**
     * Count ticket links by provider
     */
    public long countTicketLinksByProvider(String providerName) {
        return ticketLinkRepository.countByProviderName(providerName);
    }

    /**
     * Clear cache
     */
    @CacheEvict(value = {"tickets", "ticketsByMatch", "ticketsByProvider"}, allEntries = true)
    public void clearCache() {
        log.info("Ticket link cache cleared");
    }
}

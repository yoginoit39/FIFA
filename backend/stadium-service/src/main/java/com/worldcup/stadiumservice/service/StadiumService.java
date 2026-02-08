package com.worldcup.stadiumservice.service;

import com.worldcup.stadiumservice.dto.StadiumDTO;
import com.worldcup.stadiumservice.entity.Stadium;
import com.worldcup.stadiumservice.exception.ResourceNotFoundException;
import com.worldcup.stadiumservice.mapper.StadiumMapper;
import com.worldcup.stadiumservice.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Stadium operations
 * Handles business logic and caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StadiumService {

    private final StadiumRepository stadiumRepository;
    private final StadiumMapper stadiumMapper;

    /**
     * Get all stadiums with pagination
     */
    @Cacheable(value = "stadiums", key = "'page_' + #page + '_size_' + #size")
    public Page<StadiumDTO> getAllStadiums(int page, int size) {
        log.debug("Fetching stadiums - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Stadium> stadiumPage = stadiumRepository.findAll(pageable);
        return stadiumPage.map(stadiumMapper::toStadiumDTO);
    }

    /**
     * Get all stadiums (no pagination)
     */
    @Cacheable(value = "stadiums", key = "'all'")
    public List<StadiumDTO> getAllStadiums() {
        log.debug("Fetching all stadiums from database");
        List<Stadium> stadiums = stadiumRepository.findAllByOrderByNameAsc();
        return stadiumMapper.toStadiumDTOList(stadiums);
    }

    /**
     * Get stadium by ID
     */
    @Cacheable(value = "stadiums", key = "#id")
    public StadiumDTO getStadiumById(Long id) {
        log.debug("Fetching stadium by ID: {}", id);
        Stadium stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with ID: " + id));
        return stadiumMapper.toStadiumDTO(stadium);
    }

    /**
     * Get stadium by name
     */
    @Cacheable(value = "stadiums", key = "'name_' + #name")
    public StadiumDTO getStadiumByName(String name) {
        log.debug("Fetching stadium by name: {}", name);
        Stadium stadium = stadiumRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with name: " + name));
        return stadiumMapper.toStadiumDTO(stadium);
    }

    /**
     * Get stadiums by city
     */
    @Cacheable(value = "stadiumsByCity", key = "#city")
    public List<StadiumDTO> getStadiumsByCity(String city) {
        log.debug("Fetching stadiums by city: {}", city);
        List<Stadium> stadiums = stadiumRepository.findByCityOrderByNameAsc(city);
        return stadiumMapper.toStadiumDTOList(stadiums);
    }

    /**
     * Get stadiums by country
     */
    @Cacheable(value = "stadiumsByCountry", key = "#country")
    public List<StadiumDTO> getStadiumsByCountry(String country) {
        log.debug("Fetching stadiums by country: {}", country);
        List<Stadium> stadiums = stadiumRepository.findByCountryOrderByNameAsc(country);
        return stadiumMapper.toStadiumDTOList(stadiums);
    }

    /**
     * Get stadiums by state
     */
    public List<StadiumDTO> getStadiumsByState(String state) {
        log.debug("Fetching stadiums by state: {}", state);
        List<Stadium> stadiums = stadiumRepository.findByStateOrderByNameAsc(state);
        return stadiumMapper.toStadiumDTOList(stadiums);
    }

    /**
     * Get all stadiums ordered by capacity
     */
    @Cacheable(value = "stadiums", key = "'byCapacity'")
    public List<StadiumDTO> getStadiumsByCapacity() {
        log.debug("Fetching stadiums ordered by capacity");
        List<Stadium> stadiums = stadiumRepository.findAllOrderedByCapacity();
        return stadiumMapper.toStadiumDTOList(stadiums);
    }

    /**
     * Create new stadium
     */
    @Transactional
    @CacheEvict(value = {"stadiums", "stadiumsByCity", "stadiumsByCountry"}, allEntries = true)
    public StadiumDTO createStadium(StadiumDTO stadiumDTO) {
        log.info("Creating new stadium: {}", stadiumDTO.getName());

        // Check if stadium already exists
        if (stadiumDTO.getName() != null && stadiumRepository.existsByName(stadiumDTO.getName())) {
            throw new IllegalArgumentException("Stadium already exists with name: " + stadiumDTO.getName());
        }

        Stadium stadium = stadiumMapper.toStadiumEntity(stadiumDTO);
        Stadium savedStadium = stadiumRepository.save(stadium);
        log.info("Stadium created successfully with ID: {}", savedStadium.getId());

        return stadiumMapper.toStadiumDTO(savedStadium);
    }

    /**
     * Update existing stadium
     */
    @Transactional
    @CacheEvict(value = {"stadiums", "stadiumsByCity", "stadiumsByCountry"}, allEntries = true)
    public StadiumDTO updateStadium(Long id, StadiumDTO stadiumDTO) {
        log.info("Updating stadium with ID: {}", id);

        Stadium existingStadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with ID: " + id));

        stadiumMapper.updateStadiumFromDTO(stadiumDTO, existingStadium);
        Stadium updatedStadium = stadiumRepository.save(existingStadium);
        log.info("Stadium updated successfully with ID: {}", updatedStadium.getId());

        return stadiumMapper.toStadiumDTO(updatedStadium);
    }

    /**
     * Delete stadium
     */
    @Transactional
    @CacheEvict(value = {"stadiums", "stadiumsByCity", "stadiumsByCountry"}, allEntries = true)
    public void deleteStadium(Long id) {
        log.info("Deleting stadium with ID: {}", id);

        if (!stadiumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stadium not found with ID: " + id);
        }

        stadiumRepository.deleteById(id);
        log.info("Stadium deleted successfully with ID: {}", id);
    }

    /**
     * Count total stadiums
     */
    public long countStadiums() {
        return stadiumRepository.count();
    }

    /**
     * Count stadiums by country
     */
    public long countStadiumsByCountry(String country) {
        return stadiumRepository.countByCountry(country);
    }

    /**
     * Clear stadium cache
     */
    @CacheEvict(value = {"stadiums", "stadiumsByCity", "stadiumsByCountry"}, allEntries = true)
    public void clearCache() {
        log.info("Stadium cache cleared");
    }
}

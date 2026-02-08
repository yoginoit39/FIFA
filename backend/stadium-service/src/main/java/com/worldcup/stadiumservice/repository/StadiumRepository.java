package com.worldcup.stadiumservice.repository;

import com.worldcup.stadiumservice.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Stadium entity
 */
@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    /**
     * Find stadium by name
     */
    Optional<Stadium> findByName(String name);

    /**
     * Find stadiums by city
     */
    List<Stadium> findByCityOrderByNameAsc(String city);

    /**
     * Find stadiums by country
     */
    List<Stadium> findByCountryOrderByNameAsc(String country);

    /**
     * Find stadiums by state
     */
    List<Stadium> findByStateOrderByNameAsc(String state);

    /**
     * Find all stadiums ordered by capacity (descending)
     */
    @Query("SELECT s FROM Stadium s ORDER BY s.capacity DESC NULLS LAST")
    List<Stadium> findAllOrderedByCapacity();

    /**
     * Find all stadiums ordered by name
     */
    List<Stadium> findAllByOrderByNameAsc();

    /**
     * Check if stadium exists by name
     */
    boolean existsByName(String name);

    /**
     * Count stadiums by country
     */
    long countByCountry(String country);
}

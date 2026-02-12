package com.worldcup.dealfinderservice.repository;

import com.worldcup.dealfinderservice.entity.FetchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FetchLogRepository extends JpaRepository<FetchLog, Long> {

    List<FetchLog> findByProviderIdOrderByStartedAtDesc(Long providerId);
}

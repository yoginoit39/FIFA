package com.worldcup.dealfinderservice.service;

import com.worldcup.dealfinderservice.dto.ProviderDTO;
import com.worldcup.dealfinderservice.entity.Provider;
import com.worldcup.dealfinderservice.exception.ResourceNotFoundException;
import com.worldcup.dealfinderservice.mapper.ProviderMapper;
import com.worldcup.dealfinderservice.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Cacheable(value = "providers", key = "'all_active'")
    public List<ProviderDTO> getAllActiveProviders() {
        log.debug("Fetching all active providers");
        List<Provider> providers = providerRepository.findByIsActiveTrueOrderByPriorityAsc();
        return providerMapper.toDTOList(providers);
    }

    public ProviderDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + id));
        return providerMapper.toDTO(provider);
    }

    public ProviderDTO getProviderByName(String name) {
        Provider provider = providerRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found: " + name));
        return providerMapper.toDTO(provider);
    }
}

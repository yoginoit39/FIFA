package com.worldcup.dealfinderservice.mapper;

import com.worldcup.dealfinderservice.dto.ProviderDTO;
import com.worldcup.dealfinderservice.entity.Provider;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProviderMapper {

    ProviderDTO toDTO(Provider provider);

    List<ProviderDTO> toDTOList(List<Provider> providers);
}

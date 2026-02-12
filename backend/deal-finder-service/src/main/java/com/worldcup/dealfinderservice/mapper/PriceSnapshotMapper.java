package com.worldcup.dealfinderservice.mapper;

import com.worldcup.dealfinderservice.dto.PriceSnapshotDTO;
import com.worldcup.dealfinderservice.entity.PriceSnapshot;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PriceSnapshotMapper {

    @Mapping(source = "provider.name", target = "providerName")
    @Mapping(source = "provider.logoUrl", target = "providerLogoUrl")
    PriceSnapshotDTO toDTO(PriceSnapshot snapshot);

    List<PriceSnapshotDTO> toDTOList(List<PriceSnapshot> snapshots);
}

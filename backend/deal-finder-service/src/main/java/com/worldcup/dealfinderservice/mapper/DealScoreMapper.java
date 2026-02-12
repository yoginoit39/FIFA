package com.worldcup.dealfinderservice.mapper;

import com.worldcup.dealfinderservice.dto.DealScoreDTO;
import com.worldcup.dealfinderservice.entity.DealScore;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DealScoreMapper {

    @Mapping(source = "provider.name", target = "providerName")
    @Mapping(source = "provider.logoUrl", target = "providerLogoUrl")
    @Mapping(source = "provider.trustScore", target = "trustScore")
    @Mapping(source = "provider.feePercentage", target = "feePercentage")
    @Mapping(source = "provider.hasBuyerProtection", target = "hasBuyerProtection")
    DealScoreDTO toDTO(DealScore dealScore);

    List<DealScoreDTO> toDTOList(List<DealScore> dealScores);
}

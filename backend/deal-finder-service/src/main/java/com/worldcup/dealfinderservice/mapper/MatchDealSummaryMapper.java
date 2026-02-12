package com.worldcup.dealfinderservice.mapper;

import com.worldcup.dealfinderservice.dto.MatchDealSummaryDTO;
import com.worldcup.dealfinderservice.entity.MatchDealSummary;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MatchDealSummaryMapper {

    @Mapping(source = "bestProvider.name", target = "bestProviderName")
    MatchDealSummaryDTO toDTO(MatchDealSummary summary);

    List<MatchDealSummaryDTO> toDTOList(List<MatchDealSummary> summaries);
}

package com.worldcup.stadiumservice.mapper;

import com.worldcup.stadiumservice.dto.StadiumDTO;
import com.worldcup.stadiumservice.entity.Stadium;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for converting between Stadium entities and DTOs
 * MapStruct will generate the implementation at compile time
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StadiumMapper {

    /**
     * Convert Stadium entity to StadiumDTO
     */
    StadiumDTO toStadiumDTO(Stadium stadium);

    /**
     * Convert StadiumDTO to Stadium entity
     */
    Stadium toStadiumEntity(StadiumDTO stadiumDTO);

    /**
     * Convert list of Stadium entities to list of StadiumDTOs
     */
    List<StadiumDTO> toStadiumDTOList(List<Stadium> stadiums);

    /**
     * Update existing Stadium entity from StadiumDTO
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateStadiumFromDTO(StadiumDTO dto, @MappingTarget Stadium entity);
}

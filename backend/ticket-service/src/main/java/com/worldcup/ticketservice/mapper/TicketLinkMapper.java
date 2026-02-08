package com.worldcup.ticketservice.mapper;

import com.worldcup.ticketservice.dto.TicketLinkDTO;
import com.worldcup.ticketservice.entity.TicketLink;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for TicketLink
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TicketLinkMapper {

    TicketLinkDTO toTicketLinkDTO(TicketLink ticketLink);

    TicketLink toTicketLinkEntity(TicketLinkDTO ticketLinkDTO);

    List<TicketLinkDTO> toTicketLinkDTOList(List<TicketLink> ticketLinks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateTicketLinkFromDTO(TicketLinkDTO dto, @MappingTarget TicketLink entity);
}

/**
 * React Query hooks for ticket data
 */
import { useQuery } from '@tanstack/react-query';
import ticketService from '../services/ticketService';

/**
 * Get ticket links by match ID
 */
export const useTicketsByMatch = (matchId) => {
  return useQuery({
    queryKey: ['tickets', 'by-match', matchId],
    queryFn: () => ticketService.getTicketLinksByMatch(matchId),
    enabled: !!matchId,
    staleTime: 60 * 60 * 1000, // 1 hour
  });
};

/**
 * Get all ticket links
 */
export const useAllTickets = () => {
  return useQuery({
    queryKey: ['tickets', 'all'],
    queryFn: ticketService.getAllTicketLinks,
    staleTime: 60 * 60 * 1000,
  });
};

/**
 * Get ticket link by ID
 */
export const useTicket = (id) => {
  return useQuery({
    queryKey: ['ticket', id],
    queryFn: () => ticketService.getTicketLinkById(id),
    enabled: !!id,
    staleTime: 60 * 60 * 1000,
  });
};

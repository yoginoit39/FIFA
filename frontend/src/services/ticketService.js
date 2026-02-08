/**
 * Ticket Service
 * API calls for ticket booking links
 */
import api from './api';

const ticketService = {
  /**
   * Get all ticket links
   */
  getAllTicketLinks: async () => {
    const response = await api.get('/api/tickets');
    return response.data;
  },

  /**
   * Get ticket link by ID
   */
  getTicketLinkById: async (id) => {
    const response = await api.get(`/api/tickets/${id}`);
    return response.data;
  },

  /**
   * Get ticket links by match ID
   */
  getTicketLinksByMatch: async (matchId) => {
    const response = await api.get(`/api/tickets/match/${matchId}`);
    return response.data;
  },

  /**
   * Get ticket links by provider
   */
  getTicketLinksByProvider: async (providerName) => {
    const response = await api.get(`/api/tickets/provider/${providerName}`);
    return response.data;
  },

  /**
   * Get ticket links by status
   */
  getTicketLinksByStatus: async (status) => {
    const response = await api.get(`/api/tickets/status/${status}`);
    return response.data;
  },
};

export default ticketService;

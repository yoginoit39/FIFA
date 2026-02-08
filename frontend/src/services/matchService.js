/**
 * Match Service
 * API calls for match and team data
 */
import api from './api';

const matchService = {
  // ========== Match Endpoints ==========

  /**
   * Get all matches (paginated)
   */
  getAllMatches: async (page = 0, size = 20) => {
    const response = await api.get('/api/matches', {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get match by ID
   */
  getMatchById: async (id) => {
    const response = await api.get(`/api/matches/${id}`);
    return response.data;
  },

  /**
   * Get upcoming matches
   */
  getUpcomingMatches: async () => {
    const response = await api.get('/api/matches/upcoming');
    return response.data;
  },

  /**
   * Get matches by date
   */
  getMatchesByDate: async (date) => {
    const response = await api.get(`/api/matches/by-date/${date}`);
    return response.data;
  },

  /**
   * Get matches by date range
   */
  getMatchesByDateRange: async (startDate, endDate) => {
    const response = await api.get('/api/matches/by-date-range', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  /**
   * Get matches by team
   */
  getMatchesByTeam: async (teamId) => {
    const response = await api.get(`/api/matches/by-team/${teamId}`);
    return response.data;
  },

  /**
   * Get matches by stadium
   */
  getMatchesByStadium: async (stadiumId) => {
    const response = await api.get(`/api/matches/by-stadium/${stadiumId}`);
    return response.data;
  },

  /**
   * Get matches by round
   */
  getMatchesByRound: async (round) => {
    const response = await api.get(`/api/matches/by-round/${round}`);
    return response.data;
  },

  /**
   * Get matches by group
   */
  getMatchesByGroup: async (groupName) => {
    const response = await api.get(`/api/matches/by-group/${groupName}`);
    return response.data;
  },

  /**
   * Get live matches
   */
  getLiveMatches: async () => {
    const response = await api.get('/api/matches/live');
    return response.data;
  },

  /**
   * Get matches by status
   */
  getMatchesByStatus: async (status) => {
    const response = await api.get(`/api/matches/by-status/${status}`);
    return response.data;
  },

  // ========== Team Endpoints ==========

  /**
   * Get all teams
   */
  getAllTeams: async () => {
    const response = await api.get('/api/teams');
    return response.data;
  },

  /**
   * Get team by ID
   */
  getTeamById: async (id) => {
    const response = await api.get(`/api/teams/${id}`);
    return response.data;
  },

  /**
   * Get team by country
   */
  getTeamByCountry: async (country) => {
    const response = await api.get(`/api/teams/country/${country}`);
    return response.data;
  },
};

export default matchService;

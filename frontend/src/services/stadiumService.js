/**
 * Stadium Service
 * API calls for stadium data
 */
import api from './api';

const stadiumService = {
  /**
   * Get all stadiums (paginated)
   */
  getAllStadiums: async (page = 0, size = 20) => {
    const response = await api.get('/api/stadiums', {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get all stadiums (no pagination)
   */
  getAllStadiumsList: async () => {
    const response = await api.get('/api/stadiums/all');
    return response.data;
  },

  /**
   * Get stadium by ID
   */
  getStadiumById: async (id) => {
    const response = await api.get(`/api/stadiums/${id}`);
    return response.data;
  },

  /**
   * Get stadium by name
   */
  getStadiumByName: async (name) => {
    const response = await api.get(`/api/stadiums/name/${name}`);
    return response.data;
  },

  /**
   * Get stadiums by city
   */
  getStadiumsByCity: async (city) => {
    const response = await api.get(`/api/stadiums/by-city/${city}`);
    return response.data;
  },

  /**
   * Get stadiums by country
   */
  getStadiumsByCountry: async (country) => {
    const response = await api.get(`/api/stadiums/by-country/${country}`);
    return response.data;
  },

  /**
   * Get stadiums by state
   */
  getStadiumsByState: async (state) => {
    const response = await api.get(`/api/stadiums/by-state/${state}`);
    return response.data;
  },

  /**
   * Get stadiums ordered by capacity
   */
  getStadiumsByCapacity: async () => {
    const response = await api.get('/api/stadiums/by-capacity');
    return response.data;
  },
};

export default stadiumService;

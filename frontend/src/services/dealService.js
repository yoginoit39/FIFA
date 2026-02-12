import api from './api';

const dealService = {
  getDealsForMatch: async (matchId) => {
    const response = await api.get(`/api/deals/match/${matchId}`);
    return response.data;
  },

  getCheapestDeal: async (matchId) => {
    const response = await api.get(`/api/deals/match/${matchId}/cheapest`);
    return response.data;
  },

  getTopDeals: async (limit = 10) => {
    const response = await api.get('/api/deals/top', {
      params: { limit },
    });
    return response.data;
  },

  getPriceHistory: async (matchId) => {
    const response = await api.get(`/api/deals/match/${matchId}/history`);
    return response.data;
  },

  getAllSummaries: async () => {
    const response = await api.get('/api/deals/summaries');
    return response.data;
  },

  getProviders: async () => {
    const response = await api.get('/api/deals/providers');
    return response.data;
  },

  getProviderById: async (id) => {
    const response = await api.get(`/api/deals/providers/${id}`);
    return response.data;
  },
};

export default dealService;

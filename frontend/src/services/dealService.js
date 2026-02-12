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

  getMarketOverview: async () => {
    const response = await api.get('/api/deals/analytics/overview');
    return response.data;
  },

  getTrendingMatches: async (limit = 10) => {
    const response = await api.get('/api/deals/analytics/trending', {
      params: { limit },
    });
    return response.data;
  },

  getPriceDrops: async (limit = 10) => {
    const response = await api.get('/api/deals/analytics/price-drops', {
      params: { limit },
    });
    return response.data;
  },
};

export default dealService;

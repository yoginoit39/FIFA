import { useQuery } from '@tanstack/react-query';
import dealService from '../services/dealService';

export const useDealsByMatch = (matchId) => {
  return useQuery({
    queryKey: ['deals', 'match', matchId],
    queryFn: () => dealService.getDealsForMatch(matchId),
    enabled: !!matchId,
    staleTime: 15 * 60 * 1000,
  });
};

export const useCheapestDeal = (matchId) => {
  return useQuery({
    queryKey: ['deals', 'cheapest', matchId],
    queryFn: () => dealService.getCheapestDeal(matchId),
    enabled: !!matchId,
    staleTime: 15 * 60 * 1000,
  });
};

export const useTopDeals = (limit = 10) => {
  return useQuery({
    queryKey: ['deals', 'top', limit],
    queryFn: () => dealService.getTopDeals(limit),
    staleTime: 15 * 60 * 1000,
  });
};

export const usePriceHistory = (matchId) => {
  return useQuery({
    queryKey: ['deals', 'history', matchId],
    queryFn: () => dealService.getPriceHistory(matchId),
    enabled: !!matchId,
    staleTime: 15 * 60 * 1000,
  });
};

export const useDealSummaries = () => {
  return useQuery({
    queryKey: ['deals', 'summaries'],
    queryFn: dealService.getAllSummaries,
    staleTime: 15 * 60 * 1000,
  });
};

export const useProviders = () => {
  return useQuery({
    queryKey: ['deals', 'providers'],
    queryFn: dealService.getProviders,
    staleTime: 60 * 60 * 1000,
  });
};

export const useMarketOverview = () => {
  return useQuery({
    queryKey: ['deals', 'analytics', 'overview'],
    queryFn: dealService.getMarketOverview,
    staleTime: 15 * 60 * 1000,
  });
};

export const useTrendingMatches = (limit = 10) => {
  return useQuery({
    queryKey: ['deals', 'analytics', 'trending', limit],
    queryFn: () => dealService.getTrendingMatches(limit),
    staleTime: 15 * 60 * 1000,
  });
};

export const usePriceDrops = (limit = 10) => {
  return useQuery({
    queryKey: ['deals', 'analytics', 'price-drops', limit],
    queryFn: () => dealService.getPriceDrops(limit),
    staleTime: 15 * 60 * 1000,
  });
};

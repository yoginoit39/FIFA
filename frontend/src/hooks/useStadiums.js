/**
 * React Query hooks for stadium data
 */
import { useQuery } from '@tanstack/react-query';
import stadiumService from '../services/stadiumService';
import matchService from '../services/matchService';

/**
 * Get all stadiums (paginated)
 */
export const useStadiums = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['stadiums', page, size],
    queryFn: () => stadiumService.getAllStadiums(page, size),
    staleTime: 60 * 60 * 1000, // 1 hour
  });
};

/**
 * Get all stadiums (no pagination)
 */
export const useAllStadiums = () => {
  return useQuery({
    queryKey: ['stadiums', 'all'],
    queryFn: stadiumService.getAllStadiumsList,
    staleTime: 60 * 60 * 1000,
  });
};

/**
 * Get stadium by ID
 */
export const useStadium = (id) => {
  return useQuery({
    queryKey: ['stadium', id],
    queryFn: () => stadiumService.getStadiumById(id),
    enabled: !!id,
    staleTime: 60 * 60 * 1000,
  });
};

/**
 * Get stadiums by country
 */
export const useStadiumsByCountry = (country) => {
  return useQuery({
    queryKey: ['stadiums', 'by-country', country],
    queryFn: () => stadiumService.getStadiumsByCountry(country),
    enabled: !!country,
    staleTime: 60 * 60 * 1000,
  });
};

/**
 * Get stadiums by capacity
 */
export const useStadiumsByCapacity = () => {
  return useQuery({
    queryKey: ['stadiums', 'by-capacity'],
    queryFn: stadiumService.getStadiumsByCapacity,
    staleTime: 60 * 60 * 1000,
  });
};

/**
 * Get matches at a specific stadium
 */
export const useStadiumMatches = (stadiumId) => {
  return useQuery({
    queryKey: ['stadium-matches', stadiumId],
    queryFn: () => matchService.getMatchesByStadium(stadiumId),
    enabled: !!stadiumId,
    staleTime: 30 * 60 * 1000, // 30 minutes
  });
};

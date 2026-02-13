/**
 * React Query hooks for match data
 */
import { useQuery } from '@tanstack/react-query';
import matchService from '../services/matchService';

/**
 * Get all matches (paginated)
 */
export const useMatches = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['matches', page, size],
    queryFn: () => matchService.getAllMatches(page, size),
    staleTime: 30 * 60 * 1000, // 30 minutes
  });
};

/**
 * Get match by ID
 */
export const useMatch = (id) => {
  return useQuery({
    queryKey: ['match', id],
    queryFn: () => matchService.getMatchById(id),
    enabled: !!id,
    staleTime: 30 * 60 * 1000,
  });
};

/**
 * Get upcoming matches
 */
export const useUpcomingMatches = () => {
  return useQuery({
    queryKey: ['matches', 'upcoming'],
    queryFn: matchService.getUpcomingMatches,
    staleTime: 30 * 60 * 1000,
  });
};

/**
 * Get matches by date
 */
export const useMatchesByDate = (date) => {
  return useQuery({
    queryKey: ['matches', 'by-date', date],
    queryFn: () => matchService.getMatchesByDate(date),
    enabled: !!date,
    staleTime: 30 * 60 * 1000,
  });
};

/**
 * Get matches by team
 */
export const useMatchesByTeam = (teamId) => {
  return useQuery({
    queryKey: ['matches', 'by-team', teamId],
    queryFn: () => matchService.getMatchesByTeam(teamId),
    enabled: !!teamId,
    staleTime: 30 * 60 * 1000,
  });
};

/**
 * Get matches by stadium
 */
export const useMatchesByStadium = (stadiumId) => {
  return useQuery({
    queryKey: ['matches', 'by-stadium', stadiumId],
    queryFn: () => matchService.getMatchesByStadium(stadiumId),
    enabled: !!stadiumId,
    staleTime: 30 * 60 * 1000,
  });
};

/**
 * Get live matches
 */
export const useLiveMatches = () => {
  return useQuery({
    queryKey: ['matches', 'live'],
    queryFn: matchService.getLiveMatches,
    refetchInterval: 60 * 1000, // Refetch every minute
    staleTime: 60 * 1000, // 1 minute
  });
};

/**
 * Get all matches as a lookup map (matchId -> match data)
 * Useful for cross-referencing matchIds from analytics endpoints
 */
export const useMatchLookup = () => {
  const { data, ...rest } = useQuery({
    queryKey: ['matches', 0, 100],
    queryFn: () => matchService.getAllMatches(0, 100),
    staleTime: 30 * 60 * 1000,
  });

  const lookup = {};
  if (data?.content) {
    data.content.forEach((match) => {
      lookup[match.id] = match;
    });
  }

  return { lookup, ...rest };
};

/**
 * Get all teams
 */
export const useTeams = () => {
  return useQuery({
    queryKey: ['teams'],
    queryFn: matchService.getAllTeams,
    staleTime: 60 * 60 * 1000, // 1 hour
  });
};

/**
 * Get team by ID
 */
export const useTeam = (id) => {
  return useQuery({
    queryKey: ['team', id],
    queryFn: () => matchService.getTeamById(id),
    enabled: !!id,
    staleTime: 60 * 60 * 1000,
  });
};

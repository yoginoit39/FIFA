import { useState, useMemo } from 'react';
import {
  Container, Typography, Grid, Box, Tabs, Tab,
  FormControl, InputLabel, Select, MenuItem,
} from '@mui/material';
import { useUpcomingMatches, useLiveMatches } from '../hooks/useMatches';
import MatchCard from '../components/match/MatchCard';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';

const MatchesPage = () => {
  const [tabValue, setTabValue] = useState(0);
  const [filterCountry, setFilterCountry] = useState('');
  const [filterCity, setFilterCity] = useState('');

  const { data: upcomingMatches, isLoading: upcomingLoading, error: upcomingError, refetch: refetchUpcoming } = useUpcomingMatches();
  const { data: liveMatches, isLoading: liveLoading, error: liveError, refetch: refetchLive } = useLiveMatches();

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
    setFilterCountry('');
    setFilterCity('');
  };

  const activeMatches = tabValue === 0 ? (upcomingMatches || []) : (liveMatches || []);

  const countries = useMemo(() =>
    [...new Set(activeMatches.map(m => m.venueCountry).filter(Boolean))].sort(),
    [activeMatches]
  );

  const cities = useMemo(() => {
    const source = filterCountry
      ? activeMatches.filter(m => m.venueCountry === filterCountry)
      : activeMatches;
    return [...new Set(source.map(m => m.venueCity).filter(Boolean))].sort();
  }, [activeMatches, filterCountry]);

  const filteredMatches = useMemo(() =>
    activeMatches.filter(m => {
      if (filterCountry && m.venueCountry !== filterCountry) return false;
      if (filterCity && m.venueCity !== filterCity) return false;
      return true;
    }),
    [activeMatches, filterCountry, filterCity]
  );

  const renderMatches = () => {
    const isLoading = tabValue === 0 ? upcomingLoading : liveLoading;
    const error = tabValue === 0 ? upcomingError : liveError;
    const refetch = tabValue === 0 ? refetchUpcoming : refetchLive;

    if (isLoading) return <Loading message="Loading matches..." />;
    if (error) return <ErrorMessage message={error.message} onRetry={refetch} />;

    if (filteredMatches.length === 0) {
      return (
        <Box sx={{ textAlign: 'center', py: 10 }}>
          <Typography variant="h6" color="text.secondary">No matches found</Typography>
        </Box>
      );
    }

    return (
      <Grid container spacing={3}>
        {filteredMatches.map((match) => (
          <Grid item xs={12} sm={6} lg={4} key={match.id}>
            <MatchCard match={match} />
          </Grid>
        ))}
      </Grid>
    );
  };

  return (
    <Container maxWidth={false} sx={{ py: { xs: 4, md: 6 }, px: { xs: 2, sm: 4, md: 6, lg: 8 } }}>
      <Box sx={{ mb: 5 }}>
        <Typography variant="overline" sx={{ color: 'primary.main', fontWeight: 700, letterSpacing: '0.1em' }}>
          Schedule
        </Typography>
        <Typography variant="h3" component="h1" sx={{ mt: 0.5, mb: 1 }}>
          FIFA World Cup 2026
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Browse all matches, check schedules, and compare ticket prices
        </Typography>
      </Box>

      {/* Tabs */}
      <Box sx={{ borderBottom: '1px solid rgba(255,255,255,0.06)', mb: 4 }}>
        <Tabs
          value={tabValue}
          onChange={handleTabChange}
          sx={{
            '& .MuiTabs-indicator': {
              background: 'linear-gradient(90deg, #00e676, #00b248)',
              height: 3,
              borderRadius: '3px 3px 0 0',
            },
          }}
        >
          <Tab label="Upcoming Matches" />
          <Tab label="⚽ Live Now" />
        </Tabs>
      </Box>

      {/* Filters */}
      <Box sx={{ display: 'flex', gap: 2, mb: 5, flexWrap: 'wrap' }}>
        <FormControl size="small" sx={{ minWidth: 180 }}>
          <InputLabel sx={{ color: 'text.secondary' }}>Country</InputLabel>
          <Select
            value={filterCountry}
            label="Country"
            onChange={(e) => { setFilterCountry(e.target.value); setFilterCity(''); }}
            sx={{ borderRadius: 2 }}
          >
            <MenuItem value="">All Countries</MenuItem>
            {countries.map(c => <MenuItem key={c} value={c}>{c}</MenuItem>)}
          </Select>
        </FormControl>

        <FormControl size="small" sx={{ minWidth: 220 }}>
          <InputLabel sx={{ color: 'text.secondary' }}>City</InputLabel>
          <Select
            value={filterCity}
            label="City"
            onChange={(e) => setFilterCity(e.target.value)}
            sx={{ borderRadius: 2 }}
          >
            <MenuItem value="">All Cities</MenuItem>
            {cities.map(c => <MenuItem key={c} value={c}>{c}</MenuItem>)}
          </Select>
        </FormControl>

        {(filterCountry || filterCity) && (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              {filteredMatches.length} match{filteredMatches.length !== 1 ? 'es' : ''} found
            </Typography>
          </Box>
        )}
      </Box>

      {renderMatches()}
    </Container>
  );
};

export default MatchesPage;

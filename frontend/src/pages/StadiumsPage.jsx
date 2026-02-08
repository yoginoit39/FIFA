/**
 * Stadiums Page
 * Lists all FIFA World Cup 2026 stadiums
 */
import { useState } from 'react';
import {
  Container,
  Typography,
  Grid,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import { useStadiums } from '../hooks/useStadiums';
import StadiumCard from '../components/stadium/StadiumCard';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';

const StadiumsPage = () => {
  const [countryFilter, setCountryFilter] = useState('ALL');
  const { data: stadiums, isLoading, error, refetch } = useStadiums();

  if (isLoading) return <Loading message="Loading stadiums..." />;
  if (error) return <ErrorMessage message={error.message} onRetry={refetch} />;

  // Get unique countries
  const countries = ['ALL', ...new Set(stadiums?.map((s) => s.country) || [])];

  // Filter stadiums by country
  const filteredStadiums =
    countryFilter === 'ALL'
      ? stadiums
      : stadiums?.filter((s) => s.country === countryFilter);

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          FIFA World Cup 2026 Stadiums
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Explore the 16 iconic venues across USA, Canada, and Mexico
        </Typography>
      </Box>

      <Box sx={{ mb: 4 }}>
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel>Filter by Country</InputLabel>
          <Select
            value={countryFilter}
            label="Filter by Country"
            onChange={(e) => setCountryFilter(e.target.value)}
          >
            {countries.map((country) => (
              <MenuItem key={country} value={country}>
                {country === 'ALL' ? 'All Countries' : country}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {filteredStadiums && filteredStadiums.length === 0 ? (
        <Box sx={{ textAlign: 'center', py: 8 }}>
          <Typography variant="h6" color="text.secondary">
            No stadiums found
          </Typography>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {filteredStadiums?.map((stadium) => (
            <Grid item xs={12} sm={6} md={4} key={stadium.id}>
              <StadiumCard stadium={stadium} />
            </Grid>
          ))}
        </Grid>
      )}

      <Box sx={{ mt: 4, textAlign: 'center' }}>
        <Typography variant="body2" color="text.secondary">
          Total: {filteredStadiums?.length || 0} stadium(s)
        </Typography>
      </Box>
    </Container>
  );
};

export default StadiumsPage;

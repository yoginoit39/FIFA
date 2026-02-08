/**
 * Stadium Details Page
 * Detailed view of a single stadium with matches scheduled at this venue
 */
import { useParams, Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  Divider,
  Paper,
  List,
  ListItem,
  ListItemText,
} from '@mui/material';
import { useStadium, useStadiumMatches } from '../hooks/useStadiums';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';
import { formatDateTime } from '../utils/dateFormatter';
import PlaceIcon from '@mui/icons-material/Place';
import PeopleIcon from '@mui/icons-material/People';
import PublicIcon from '@mui/icons-material/Public';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import SportsIcon from '@mui/icons-material/Sports';

const StadiumDetailsPage = () => {
  const { id } = useParams();
  const { data: stadium, isLoading: stadiumLoading, error: stadiumError, refetch: refetchStadium } = useStadium(id);
  const { data: matches, isLoading: matchesLoading, error: matchesError } = useStadiumMatches(id);

  if (stadiumLoading) return <Loading message="Loading stadium details..." />;
  if (stadiumError) return <ErrorMessage message={stadiumError.message} onRetry={refetchStadium} />;
  if (!stadium) return <ErrorMessage message="Stadium not found" />;

  return (
    <Container maxWidth="lg">
      <Button
        component={RouterLink}
        to="/stadiums"
        startIcon={<ArrowBackIcon />}
        sx={{ mb: 3 }}
      >
        Back to Stadiums
      </Button>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h3" component="h1" gutterBottom>
            {stadium.name}
          </Typography>

          <Grid container spacing={3} sx={{ mt: 2 }}>
            <Grid item xs={12} md={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <PlaceIcon color="action" />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Location
                  </Typography>
                  <Typography variant="body1">
                    {stadium.city}, {stadium.state || stadium.country}
                  </Typography>
                  {stadium.address && (
                    <Typography variant="body2" color="text.secondary">
                      {stadium.address}
                    </Typography>
                  )}
                </Box>
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <PublicIcon color="action" />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Country
                  </Typography>
                  <Typography variant="body1">{stadium.country}</Typography>
                </Box>
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <PeopleIcon color="action" />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Capacity
                  </Typography>
                  <Typography variant="body1">
                    {stadium.capacity?.toLocaleString() || 'N/A'} spectators
                  </Typography>
                </Box>
              </Box>
            </Grid>

            {(stadium.latitude && stadium.longitude) && (
              <Grid item xs={12} md={6}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                  <PlaceIcon color="action" />
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Coordinates
                    </Typography>
                    <Typography variant="body2">
                      {stadium.latitude}, {stadium.longitude}
                    </Typography>
                  </Box>
                </Box>
              </Grid>
            )}
          </Grid>

          {stadium.description && (
            <>
              <Divider sx={{ my: 3 }} />
              <Typography variant="h6" gutterBottom>
                About
              </Typography>
              <Typography variant="body1" color="text.secondary">
                {stadium.description}
              </Typography>
            </>
          )}
        </CardContent>
      </Card>

      {/* Matches at this Stadium */}
      <Paper sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
          <SportsIcon color="primary" />
          <Typography variant="h5">Matches at this Venue</Typography>
        </Box>

        {matchesLoading ? (
          <Loading message="Loading matches..." />
        ) : matchesError ? (
          <Typography color="text.secondary">
            Unable to load matches for this stadium
          </Typography>
        ) : matches && matches.length > 0 ? (
          <List>
            {matches.map((match) => (
              <ListItem
                key={match.id}
                sx={{
                  border: '1px solid',
                  borderColor: 'divider',
                  borderRadius: 1,
                  mb: 2,
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <ListItemText
                  primary={
                    <Typography variant="h6">
                      {match.homeTeam?.name || 'TBD'} vs {match.awayTeam?.name || 'TBD'}
                    </Typography>
                  }
                  secondary={
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        {formatDateTime(match.matchDate, match.matchTime)}
                      </Typography>
                      {match.round && (
                        <Typography variant="body2" color="primary">
                          {match.round}
                        </Typography>
                      )}
                    </Box>
                  }
                />
                <Button
                  component={RouterLink}
                  to={`/matches/${match.id}`}
                  variant="contained"
                  color="secondary"
                >
                  View Match
                </Button>
              </ListItem>
            ))}
          </List>
        ) : (
          <Typography color="text.secondary">
            No matches scheduled at this stadium yet. Check back later!
          </Typography>
        )}
      </Paper>
    </Container>
  );
};

export default StadiumDetailsPage;

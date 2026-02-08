import { Container, Typography, Box, Button, Grid } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useUpcomingMatches, useLiveMatches } from '../hooks/useMatches';
import MatchCard from '../components/match/MatchCard';
import Loading from '../components/common/Loading';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import StadiumIcon from '@mui/icons-material/Stadium';
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

const StatBadge = ({ value, label }) => (
  <Box sx={{ textAlign: 'center', px: { xs: 2, md: 4 } }}>
    <Typography
      variant="h3"
      sx={{
        fontWeight: 800,
        background: 'linear-gradient(135deg, #00e676, #00b248)',
        backgroundClip: 'text',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent',
        lineHeight: 1,
        mb: 0.5,
      }}
    >
      {value}
    </Typography>
    <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>
      {label}
    </Typography>
  </Box>
);

const HomePage = () => {
  const { data: upcomingMatches, isLoading } = useUpcomingMatches();
  const { data: liveMatches } = useLiveMatches();

  const featuredMatches = upcomingMatches?.slice(0, 6) || [];
  const hasLive = liveMatches && liveMatches.length > 0;

  return (
    <Box>
      {/* Hero Section */}
      <Box
        sx={{
          position: 'relative',
          overflow: 'hidden',
          py: { xs: 10, md: 16 },
          textAlign: 'center',
          background: 'linear-gradient(180deg, #060b14 0%, #081220 40%, #071510 75%, #060b14 100%)',
          '&::before': {
            content: '""',
            position: 'absolute',
            inset: 0,
            background: 'radial-gradient(ellipse 70% 60% at 50% 0%, rgba(0,230,118,0.07) 0%, transparent 70%)',
            pointerEvents: 'none',
          },
          '&::after': {
            content: '""',
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: '1px',
            background: 'linear-gradient(90deg, transparent, rgba(0,230,118,0.2), transparent)',
          },
        }}
      >
        <Container maxWidth="md" sx={{ position: 'relative' }}>
          {/* Live indicator */}
          {hasLive && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3 }}>
              <Box
                sx={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 1,
                  px: 2,
                  py: 0.75,
                  borderRadius: 20,
                  backgroundColor: 'rgba(255,82,82,0.1)',
                  border: '1px solid rgba(255,82,82,0.3)',
                }}
              >
                <Box
                  sx={{
                    width: 8,
                    height: 8,
                    borderRadius: '50%',
                    backgroundColor: '#ff5252',
                    animation: 'pulse-live 1.5s infinite',
                  }}
                />
                <Typography variant="caption" sx={{ color: '#ff5252', fontWeight: 700, letterSpacing: '0.1em' }}>
                  {liveMatches.length} MATCH{liveMatches.length > 1 ? 'ES' : ''} LIVE NOW
                </Typography>
              </Box>
            </Box>
          )}

          <Typography
            variant="h1"
            sx={{
              fontSize: { xs: '2.5rem', md: '4rem', lg: '4.5rem' },
              mb: 2,
              background: 'linear-gradient(135deg, #ffffff 0%, #94a3b8 100%)',
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            FIFA World Cup
          </Typography>
          <Typography
            variant="h1"
            sx={{
              fontSize: { xs: '3rem', md: '5rem', lg: '6rem' },
              lineHeight: 0.9,
              mb: 3,
              background: 'linear-gradient(135deg, #00e676 0%, #69ff9c 50%, #00b248 100%)',
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            2026
          </Typography>

          <Typography
            variant="h5"
            sx={{ color: 'text.secondary', mb: 5, fontWeight: 400, fontSize: { xs: '1rem', md: '1.25rem' } }}
          >
            🇺🇸 United States &nbsp;·&nbsp; 🇨🇦 Canada &nbsp;·&nbsp; 🇲🇽 Mexico
          </Typography>

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', flexWrap: 'wrap', mb: 8 }}>
            <Button
              variant="contained"
              size="large"
              component={RouterLink}
              to="/matches"
              startIcon={<SportsSoccerIcon />}
              sx={{ px: 4, py: 1.5, fontSize: '1rem' }}
            >
              View All Matches
            </Button>
            <Button
              variant="outlined"
              size="large"
              component={RouterLink}
              to="/stadiums"
              startIcon={<StadiumIcon />}
              sx={{
                px: 4,
                py: 1.5,
                fontSize: '1rem',
                borderColor: 'rgba(255,255,255,0.15)',
                color: 'text.secondary',
                '&:hover': {
                  borderColor: 'rgba(255,255,255,0.4)',
                  color: 'white',
                  backgroundColor: 'rgba(255,255,255,0.04)',
                },
              }}
            >
              Explore Stadiums
            </Button>
          </Box>

          {/* Stats */}
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'center',
              gap: { xs: 1, md: 0 },
              flexWrap: 'wrap',
              borderTop: '1px solid rgba(255,255,255,0.06)',
              pt: 4,
            }}
          >
            <StatBadge value="48" label="Total Matches" />
            <Box sx={{ width: '1px', backgroundColor: 'rgba(255,255,255,0.06)', display: { xs: 'none', md: 'block' } }} />
            <StatBadge value="16" label="Stadiums" />
            <Box sx={{ width: '1px', backgroundColor: 'rgba(255,255,255,0.06)', display: { xs: 'none', md: 'block' } }} />
            <StatBadge value="3" label="Countries" />
            <Box sx={{ width: '1px', backgroundColor: 'rgba(255,255,255,0.06)', display: { xs: 'none', md: 'block' } }} />
            <StatBadge value="32" label="Teams" />
          </Box>
        </Container>
      </Box>

      {/* Featured Matches */}
      <Container maxWidth={false} sx={{ py: { xs: 6, md: 10 }, px: { xs: 2, sm: 4, md: 6, lg: 8 } }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', mb: 4 }}>
          <Box>
            <Typography variant="overline" sx={{ color: 'primary.main', fontWeight: 700, letterSpacing: '0.1em' }}>
              Schedule
            </Typography>
            <Typography variant="h4" sx={{ mt: 0.5 }}>
              Upcoming Matches
            </Typography>
          </Box>
          <Button
            component={RouterLink}
            to="/matches"
            endIcon={<TrendingUpIcon />}
            sx={{ color: 'primary.main', fontWeight: 600 }}
          >
            View All
          </Button>
        </Box>

        {isLoading ? (
          <Loading message="Loading matches..." />
        ) : featuredMatches.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary">No upcoming matches scheduled</Typography>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {featuredMatches.map((match) => (
              <Grid item xs={12} sm={6} lg={4} key={match.id}>
                <MatchCard match={match} />
              </Grid>
            ))}
          </Grid>
        )}

        {/* Bottom CTA */}
        <Box
          sx={{
            mt: 8,
            p: { xs: 4, md: 6 },
            borderRadius: 3,
            background: 'linear-gradient(135deg, #0f1924 0%, #141e30 100%)',
            border: '1px solid rgba(255,255,255,0.06)',
            textAlign: 'center',
          }}
        >
          <ConfirmationNumberIcon sx={{ fontSize: 48, color: 'secondary.main', mb: 2 }} />
          <Typography variant="h4" gutterBottom>
            Get Your Tickets Now
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 3, maxWidth: 500, mx: 'auto' }}>
            Compare prices across FIFA Official, Ticketmaster, StubHub, and SeatGeek to find the best deal.
          </Typography>
          <Button
            variant="contained"
            size="large"
            component={RouterLink}
            to="/matches"
            startIcon={<ConfirmationNumberIcon />}
            sx={{
              background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
              color: '#000',
              fontWeight: 700,
              px: 4,
              '&:hover': { background: 'linear-gradient(135deg, #ffd54f, #ffc107)' },
            }}
          >
            Compare Ticket Prices
          </Button>
        </Box>
      </Container>
    </Box>
  );
};

export default HomePage;

import { useParams, Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Chip,
  Button,
  Divider,
  List,
  ListItem,
  ListItemText,
  Grid,
} from '@mui/material';
import { useMatch } from '../hooks/useMatches';
import { useTicketsByMatch } from '../hooks/useTickets';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';
import { formatDateTime } from '../utils/dateFormatter';
import PlaceIcon from '@mui/icons-material/Place';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';

const FLAG_MAP = {
  'United States': '🇺🇸',
  'Mexico': '🇲🇽',
  'Canada': '🇨🇦',
  'Brazil': '🇧🇷',
  'Argentina': '🇦🇷',
  'France': '🇫🇷',
  'Germany': '🇩🇪',
  'England': '🏴󠁧󠁢󠁥󠁮󠁧󠁿',
  'Spain': '🇪🇸',
  'Portugal': '🇵🇹',
};

const MatchDetailsPage = () => {
  const { id } = useParams();
  const { data: match, isLoading: matchLoading, error: matchError, refetch: refetchMatch } = useMatch(id);
  const { data: tickets, isLoading: ticketsLoading, error: ticketsError } = useTicketsByMatch(id);

  if (matchLoading) return <Loading message="Loading match details..." />;
  if (matchError) return <ErrorMessage message={matchError.message} onRetry={refetchMatch} />;
  if (!match) return <ErrorMessage message="Match not found" />;

  const isLive = match.status === 'LIVE';
  const isFinished = match.status === 'FINISHED';

  const formatAvailability = (status) => {
    switch (status) {
      case 'AVAILABLE': return 'Available';
      case 'SOLD_OUT': return 'Sold Out';
      case 'NOT_YET_AVAILABLE': return 'Not Yet Available';
      default: return status;
    }
  };

  const sortedTickets = tickets
    ? [...tickets].sort((a, b) => {
        if (a.availabilityStatus === 'SOLD_OUT') return 1;
        if (b.availabilityStatus === 'SOLD_OUT') return -1;
        return (a.minPrice || 0) - (b.minPrice || 0);
      })
    : [];

  const availableTickets = sortedTickets.filter(t => t.availabilityStatus !== 'SOLD_OUT');

  return (
    <Box>
      {/* Hero Banner */}
      <Box
        sx={{
          position: 'relative',
          overflow: 'hidden',
          py: { xs: 6, md: 10 },
          background: isLive
            ? 'linear-gradient(180deg, #1a0808 0%, #0f1924 100%)'
            : 'linear-gradient(180deg, #060f1a 0%, #0f1924 100%)',
          borderBottom: '1px solid rgba(255,255,255,0.06)',
          '&::before': {
            content: '""',
            position: 'absolute',
            inset: 0,
            background: isLive
              ? 'radial-gradient(ellipse 60% 50% at 50% 0%, rgba(255,82,82,0.06) 0%, transparent 70%)'
              : 'radial-gradient(ellipse 60% 50% at 50% 0%, rgba(0,230,118,0.05) 0%, transparent 70%)',
            pointerEvents: 'none',
          },
        }}
      >
        <Container maxWidth="lg" sx={{ position: 'relative' }}>
          <Button
            component={RouterLink}
            to="/matches"
            startIcon={<ArrowBackIcon />}
            sx={{ mb: 4, color: 'text.secondary', '&:hover': { color: 'white' } }}
          >
            Back to Matches
          </Button>

          {/* Round & Status */}
          <Box sx={{ display: 'flex', gap: 2, mb: 4, flexWrap: 'wrap', alignItems: 'center' }}>
            <Typography
              variant="caption"
              sx={{
                color: 'primary.main',
                fontWeight: 700,
                textTransform: 'uppercase',
                letterSpacing: '0.12em',
                fontSize: '0.75rem',
              }}
            >
              {match.round || 'Tournament'}
            </Typography>
            {isLive ? (
              <Chip
                label="⚽ LIVE"
                sx={{
                  backgroundColor: 'rgba(255,82,82,0.15)',
                  color: '#ff5252',
                  fontWeight: 700,
                  border: '1px solid rgba(255,82,82,0.3)',
                  animation: 'pulse-live 2s infinite',
                }}
              />
            ) : (
              <Chip
                label={match.status}
                sx={{
                  backgroundColor: isFinished ? 'rgba(0,230,118,0.1)' : 'rgba(255,255,255,0.05)',
                  color: isFinished ? 'primary.main' : 'text.secondary',
                  fontWeight: 600,
                  border: `1px solid ${isFinished ? 'rgba(0,230,118,0.2)' : 'rgba(255,255,255,0.08)'}`,
                }}
              />
            )}
          </Box>

          {/* Teams */}
          <Grid container spacing={2} alignItems="center" sx={{ textAlign: 'center' }}>
            <Grid item xs={12} md={5}>
              <Typography sx={{ fontSize: { xs: '4rem', md: '5rem' }, lineHeight: 1, mb: 1 }}>
                {FLAG_MAP[match.homeTeam?.name] || '🏳️'}
              </Typography>
              <Typography variant="h3" sx={{ fontWeight: 800, mb: 0.5 }}>
                {match.homeTeam?.name || 'TBD'}
              </Typography>
              <Typography color="text.secondary">{match.homeTeam?.country}</Typography>
              {(isLive || isFinished) && (
                <Typography
                  variant="h1"
                  sx={{
                    fontWeight: 900,
                    fontSize: { xs: '4rem', md: '6rem' },
                    background: 'linear-gradient(135deg, #00e676, #00b248)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    mt: 1,
                  }}
                >
                  {match.homeScore}
                </Typography>
              )}
            </Grid>

            <Grid item xs={12} md={2}>
              <Typography variant="h4" sx={{ fontWeight: 800, color: 'text.secondary', letterSpacing: '0.1em' }}>
                {isFinished ? '–' : 'VS'}
              </Typography>
            </Grid>

            <Grid item xs={12} md={5}>
              <Typography sx={{ fontSize: { xs: '4rem', md: '5rem' }, lineHeight: 1, mb: 1 }}>
                {FLAG_MAP[match.awayTeam?.name] || '🏳️'}
              </Typography>
              <Typography variant="h3" sx={{ fontWeight: 800, mb: 0.5 }}>
                {match.awayTeam?.name || 'TBD'}
              </Typography>
              <Typography color="text.secondary">{match.awayTeam?.country}</Typography>
              {(isLive || isFinished) && (
                <Typography
                  variant="h1"
                  sx={{
                    fontWeight: 900,
                    fontSize: { xs: '4rem', md: '6rem' },
                    background: 'linear-gradient(135deg, #00e676, #00b248)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    mt: 1,
                  }}
                >
                  {match.awayScore}
                </Typography>
              )}
            </Grid>
          </Grid>

          {/* Match Meta */}
          <Box sx={{ display: 'flex', gap: 4, mt: 5, justifyContent: 'center', flexWrap: 'wrap' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <CalendarTodayIcon sx={{ color: 'primary.main', fontSize: 18 }} />
              <Typography color="text.secondary">{formatDateTime(match.matchDate, match.matchTime)}</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <PlaceIcon sx={{ color: 'primary.main', fontSize: 18 }} />
              <Typography color="text.secondary">{match.venueName}, {match.venueCity}, {match.venueCountry}</Typography>
            </Box>
            {match.groupName && match.groupName !== 'N/A' && (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography color="text.secondary">Group: <strong style={{ color: '#f1f5f9' }}>{match.groupName}</strong></Typography>
              </Box>
            )}
          </Box>
        </Container>
      </Box>

      {/* Ticket Comparison */}
      <Container maxWidth="lg" sx={{ py: { xs: 4, md: 6 } }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1 }}>
          <Box
            sx={{
              width: 36,
              height: 36,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <ConfirmationNumberIcon sx={{ color: '#000', fontSize: 18 }} />
          </Box>
          <Typography variant="h5">Compare Ticket Prices</Typography>
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
          Sorted by lowest price — find the best deal across providers
        </Typography>

        {ticketsLoading ? (
          <Loading message="Loading ticket options..." />
        ) : ticketsError || !tickets ? (
          <Typography color="text.secondary">Ticket information not available</Typography>
        ) : sortedTickets.length > 0 ? (
          <List disablePadding sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
            {sortedTickets.map((ticket, index) => {
              const isBest = index === 0 && ticket.availabilityStatus === 'AVAILABLE' && availableTickets.length > 1;
              const isSoldOut = ticket.availabilityStatus === 'SOLD_OUT';
              return (
                <ListItem
                  key={ticket.id}
                  sx={{
                    borderRadius: 2,
                    border: '1px solid',
                    borderColor: isBest ? 'rgba(0,230,118,0.3)' : isSoldOut ? 'rgba(255,255,255,0.04)' : 'rgba(255,255,255,0.06)',
                    background: isBest
                      ? 'linear-gradient(135deg, rgba(0,230,118,0.05), rgba(0,178,72,0.03))'
                      : 'transparent',
                    opacity: isSoldOut ? 0.5 : 1,
                    px: 3,
                    py: 2,
                  }}
                >
                  <ListItemText
                    primary={
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 0.5 }}>
                        <Typography variant="h6" sx={{ fontWeight: 700 }}>{ticket.providerName}</Typography>
                        {isBest && (
                          <Chip
                            icon={<LocalOfferIcon sx={{ fontSize: '14px !important' }} />}
                            label="Best Deal"
                            size="small"
                            sx={{
                              backgroundColor: 'rgba(0,230,118,0.15)',
                              color: 'primary.main',
                              border: '1px solid rgba(0,230,118,0.3)',
                              fontWeight: 700,
                            }}
                          />
                        )}
                      </Box>
                    }
                    secondary={
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 0.5 }}>
                        <Typography
                          variant="h6"
                          sx={{
                            fontWeight: 800,
                            color: isBest ? 'primary.main' : isSoldOut ? 'text.secondary' : 'text.primary',
                            fontSize: '1.1rem',
                          }}
                        >
                          {isSoldOut ? 'Sold Out' : ticket.priceRange}
                        </Typography>
                        <Chip
                          label={formatAvailability(ticket.availabilityStatus)}
                          size="small"
                          sx={{
                            backgroundColor: ticket.availabilityStatus === 'AVAILABLE'
                              ? 'rgba(0,230,118,0.1)'
                              : ticket.availabilityStatus === 'SOLD_OUT'
                              ? 'rgba(255,82,82,0.1)'
                              : 'rgba(255,255,255,0.05)',
                            color: ticket.availabilityStatus === 'AVAILABLE'
                              ? 'primary.main'
                              : ticket.availabilityStatus === 'SOLD_OUT'
                              ? '#ff5252'
                              : 'text.secondary',
                            fontWeight: 600,
                            fontSize: '0.7rem',
                          }}
                        />
                      </Box>
                    }
                  />
                  <Button
                    variant={isBest ? 'contained' : 'outlined'}
                    endIcon={<OpenInNewIcon />}
                    href={ticket.bookingUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    disabled={isSoldOut}
                    sx={isBest ? {
                      background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
                      color: '#000',
                      fontWeight: 700,
                      '&:hover': { background: 'linear-gradient(135deg, #ffd54f, #ffc107)' },
                    } : {
                      borderColor: 'rgba(255,255,255,0.1)',
                      color: 'text.secondary',
                      '&:hover': { borderColor: 'rgba(255,255,255,0.3)', color: 'white' },
                    }}
                  >
                    Book Now
                  </Button>
                </ListItem>
              );
            })}
          </List>
        ) : (
          <Box
            sx={{
              textAlign: 'center',
              py: 6,
              border: '1px solid rgba(255,255,255,0.06)',
              borderRadius: 2,
            }}
          >
            <ConfirmationNumberIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
            <Typography color="text.secondary">Tickets will be available soon. Check back later!</Typography>
          </Box>
        )}
      </Container>
    </Box>
  );
};

export default MatchDetailsPage;

import { Card, CardContent, CardActions, Typography, Box, Button, Chip } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { formatDateTime } from '../../utils/dateFormatter';
import PlaceIcon from '@mui/icons-material/Place';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

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

const MatchCard = ({ match }) => {
  const isLive = match.status === 'LIVE';
  const isFinished = match.status === 'FINISHED';

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        background: 'linear-gradient(160deg, #0f1924 0%, #141e30 100%)',
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        '&:hover': {
          transform: 'translateY(-6px)',
          boxShadow: isLive
            ? '0 20px 60px rgba(255, 82, 82, 0.15)'
            : '0 20px 60px rgba(0, 230, 118, 0.1)',
          borderColor: isLive ? 'rgba(255,82,82,0.3)' : 'rgba(0,230,118,0.2)',
        },
      }}
    >
      <CardContent sx={{ flexGrow: 1, p: 3 }}>
        {/* Header */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography
            variant="caption"
            sx={{
              color: 'primary.main',
              fontWeight: 700,
              textTransform: 'uppercase',
              letterSpacing: '0.1em',
              fontSize: '0.65rem',
            }}
          >
            {match.round || 'Tournament'}
          </Typography>
          {isLive ? (
            <Chip
              label="⚽ LIVE"
              size="small"
              sx={{
                backgroundColor: 'rgba(255,82,82,0.15)',
                color: '#ff5252',
                fontWeight: 700,
                fontSize: '0.7rem',
                border: '1px solid rgba(255,82,82,0.3)',
                animation: 'pulse-live 2s infinite',
              }}
            />
          ) : (
            <Chip
              label={match.status}
              size="small"
              sx={{
                backgroundColor: isFinished ? 'rgba(0,230,118,0.1)' : 'rgba(255,255,255,0.05)',
                color: isFinished ? 'primary.main' : 'text.secondary',
                fontWeight: 600,
                fontSize: '0.7rem',
                border: `1px solid ${isFinished ? 'rgba(0,230,118,0.2)' : 'rgba(255,255,255,0.08)'}`,
              }}
            />
          )}
        </Box>

        {/* Teams */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Box sx={{ flex: 1, textAlign: 'center' }}>
            <Typography sx={{ fontSize: '2.2rem', lineHeight: 1, mb: 0.75 }}>
              {FLAG_MAP[match.homeTeam?.name] || '🏳️'}
            </Typography>
            <Typography variant="body2" sx={{ fontWeight: 700, color: 'text.primary', fontSize: '0.85rem' }} noWrap>
              {match.homeTeam?.name || 'TBD'}
            </Typography>
            {isFinished && (
              <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.main', mt: 0.5, lineHeight: 1 }}>
                {match.homeScore}
              </Typography>
            )}
          </Box>

          <Box sx={{ textAlign: 'center', px: 1 }}>
            {isFinished ? (
              <Typography variant="h5" sx={{ fontWeight: 700, color: 'text.secondary' }}>–</Typography>
            ) : (
              <Box>
                <Typography
                  variant="caption"
                  sx={{
                    fontWeight: 800,
                    color: 'text.secondary',
                    letterSpacing: '0.15em',
                    fontSize: '0.75rem',
                  }}
                >
                  VS
                </Typography>
              </Box>
            )}
          </Box>

          <Box sx={{ flex: 1, textAlign: 'center' }}>
            <Typography sx={{ fontSize: '2.2rem', lineHeight: 1, mb: 0.75 }}>
              {FLAG_MAP[match.awayTeam?.name] || '🏳️'}
            </Typography>
            <Typography variant="body2" sx={{ fontWeight: 700, color: 'text.primary', fontSize: '0.85rem' }} noWrap>
              {match.awayTeam?.name || 'TBD'}
            </Typography>
            {isFinished && (
              <Typography variant="h3" sx={{ fontWeight: 800, color: 'primary.main', mt: 0.5, lineHeight: 1 }}>
                {match.awayScore}
              </Typography>
            )}
          </Box>
        </Box>

        {/* Match Info */}
        <Box sx={{ borderTop: '1px solid rgba(255,255,255,0.06)', pt: 2 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.75, mb: 0.5 }}>
            <CalendarTodayIcon sx={{ fontSize: 13, color: 'text.secondary' }} />
            <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.75rem' }}>
              {formatDateTime(match.matchDate, match.matchTime)}
            </Typography>
          </Box>
          {match.venueName && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.75 }}>
              <PlaceIcon sx={{ fontSize: 13, color: 'text.secondary' }} />
              <Typography variant="caption" color="text.secondary" noWrap sx={{ fontSize: '0.75rem' }}>
                {match.venueName}, {match.venueCity}
              </Typography>
            </Box>
          )}
        </Box>
      </CardContent>

      <CardActions sx={{ p: 2, pt: 0 }}>
        <Button
          component={RouterLink}
          to={`/matches/${match.id}`}
          fullWidth
          variant="outlined"
          endIcon={<ArrowForwardIcon />}
          sx={{
            borderColor: 'rgba(255,255,255,0.08)',
            color: 'text.secondary',
            py: 1,
            '&:hover': {
              borderColor: 'primary.main',
              color: 'primary.main',
              backgroundColor: 'rgba(0,230,118,0.04)',
            },
          }}
        >
          View Details & Tickets
        </Button>
      </CardActions>
    </Card>
  );
};

export default MatchCard;

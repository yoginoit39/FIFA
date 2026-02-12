import { Container, Typography, Box, Grid, Card, CardContent, Chip, Button, LinearProgress } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useTopDeals, useDealSummaries } from '../hooks/useDeals';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingFlatIcon from '@mui/icons-material/TrendingFlat';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import VerifiedIcon from '@mui/icons-material/Verified';

const TrendIcon = ({ trend }) => {
  if (trend === 'DOWN') return <TrendingDownIcon sx={{ color: '#00e676', fontSize: 20 }} />;
  if (trend === 'UP') return <TrendingUpIcon sx={{ color: '#ff5252', fontSize: 20 }} />;
  return <TrendingFlatIcon sx={{ color: '#94a3b8', fontSize: 20 }} />;
};

const getDealScoreColor = (score) => {
  if (score >= 70) return '#00e676';
  if (score >= 40) return '#ffc107';
  return '#ff5252';
};

const DealsPage = () => {
  const { data: topDeals, isLoading: dealsLoading, error: dealsError, refetch } = useTopDeals(20);
  const { data: summaries } = useDealSummaries();

  const matchCount = summaries?.length || 0;
  const lowestOverall = summaries?.length > 0
    ? Math.min(...summaries.map(s => s.lowestPrice))
    : null;
  const providerCount = summaries?.length > 0
    ? Math.max(...summaries.map(s => s.numProviders))
    : 0;

  return (
    <Box>
      {/* Hero Banner */}
      <Box
        sx={{
          position: 'relative',
          overflow: 'hidden',
          py: { xs: 8, md: 12 },
          textAlign: 'center',
          background: 'linear-gradient(180deg, #060b14 0%, #0d1520 40%, #0f1210 75%, #060b14 100%)',
          '&::before': {
            content: '""',
            position: 'absolute',
            inset: 0,
            background: 'radial-gradient(ellipse 70% 60% at 50% 0%, rgba(255,193,7,0.06) 0%, transparent 70%)',
            pointerEvents: 'none',
          },
          '&::after': {
            content: '""',
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: '1px',
            background: 'linear-gradient(90deg, transparent, rgba(255,193,7,0.2), transparent)',
          },
        }}
      >
        <Container maxWidth="md" sx={{ position: 'relative' }}>
          <Box
            sx={{
              width: 56,
              height: 56,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              mx: 'auto',
              mb: 3,
              boxShadow: '0 0 24px rgba(255,193,7,0.3)',
            }}
          >
            <LocalOfferIcon sx={{ color: '#000', fontSize: 28 }} />
          </Box>
          <Typography
            variant="h1"
            sx={{
              fontSize: { xs: '2.5rem', md: '3.5rem' },
              mb: 2,
              background: 'linear-gradient(135deg, #ffffff 0%, #94a3b8 100%)',
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            Best Ticket Deals
          </Typography>
          <Typography
            variant="h5"
            sx={{ color: 'text.secondary', mb: 4, fontWeight: 400, fontSize: { xs: '1rem', md: '1.15rem' } }}
          >
            AI-scored deals across all FIFA World Cup 2026 matches
          </Typography>

          {/* Stats */}
          {summaries && summaries.length > 0 && (
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'center',
                gap: { xs: 3, md: 6 },
                flexWrap: 'wrap',
                borderTop: '1px solid rgba(255,255,255,0.06)',
                pt: 4,
              }}
            >
              <Box sx={{ textAlign: 'center' }}>
                <Typography
                  variant="h4"
                  sx={{
                    fontWeight: 800,
                    background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    lineHeight: 1,
                    mb: 0.5,
                  }}
                >
                  {matchCount}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>
                  Matches Tracked
                </Typography>
              </Box>
              {lowestOverall && (
                <Box sx={{ textAlign: 'center' }}>
                  <Typography
                    variant="h4"
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
                    ${lowestOverall.toFixed(0)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>
                    Lowest Price
                  </Typography>
                </Box>
              )}
              <Box sx={{ textAlign: 'center' }}>
                <Typography
                  variant="h4"
                  sx={{
                    fontWeight: 800,
                    background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    lineHeight: 1,
                    mb: 0.5,
                  }}
                >
                  {providerCount}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>
                  Providers
                </Typography>
              </Box>
            </Box>
          )}
        </Container>
      </Box>

      {/* Deals Grid */}
      <Container maxWidth={false} sx={{ py: { xs: 6, md: 10 }, px: { xs: 2, sm: 4, md: 6, lg: 8 } }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', mb: 4 }}>
          <Box>
            <Typography variant="overline" sx={{ color: 'secondary.main', fontWeight: 700, letterSpacing: '0.1em' }}>
              Deal Scores
            </Typography>
            <Typography variant="h4" sx={{ mt: 0.5 }}>
              Top Deals
            </Typography>
          </Box>
        </Box>

        {dealsLoading ? (
          <Loading message="Analyzing deals..." />
        ) : dealsError ? (
          <ErrorMessage message="Could not load deals. The deal finder service may still be initializing." onRetry={refetch} />
        ) : !topDeals || topDeals.length === 0 ? (
          <Box
            sx={{
              textAlign: 'center',
              py: 10,
              border: '1px solid rgba(255,255,255,0.06)',
              borderRadius: 3,
            }}
          >
            <LocalOfferIcon sx={{ fontSize: 56, color: 'text.secondary', mb: 2 }} />
            <Typography variant="h6" color="text.secondary" gutterBottom>
              No deals available yet
            </Typography>
            <Typography color="text.secondary" sx={{ maxWidth: 400, mx: 'auto' }}>
              Deal scores are computed periodically. Check back soon for the best ticket deals across all matches.
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {topDeals.map((deal, index) => {
              const scoreColor = getDealScoreColor(deal.dealScore);
              const hasSavings = deal.savingsPercentage > 0;
              return (
                <Grid item xs={12} sm={6} lg={4} key={deal.id || index}>
                  <Card
                    sx={{
                      height: '100%',
                      display: 'flex',
                      flexDirection: 'column',
                      background: index === 0
                        ? 'linear-gradient(160deg, rgba(0,230,118,0.06) 0%, #0f1924 40%)'
                        : 'linear-gradient(160deg, #111d2e 0%, #0f1924 40%)',
                      border: '1px solid',
                      borderColor: index === 0 ? 'rgba(0,230,118,0.2)' : 'rgba(255,255,255,0.06)',
                      transition: 'all 0.3s ease',
                      '&:hover': {
                        transform: 'translateY(-6px)',
                        boxShadow: `0 12px 40px ${index === 0 ? 'rgba(0,230,118,0.15)' : 'rgba(0,0,0,0.4)'}`,
                        borderColor: index === 0 ? 'rgba(0,230,118,0.4)' : 'rgba(255,255,255,0.12)',
                      },
                    }}
                  >
                    <CardContent sx={{ p: 3, flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                      {/* Header: Provider + Score */}
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2.5 }}>
                        <Box>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                            <Typography variant="h6" sx={{ fontWeight: 700 }}>
                              {deal.providerName}
                            </Typography>
                            {deal.hasBuyerProtection && (
                              <VerifiedIcon sx={{ color: 'primary.main', fontSize: 18 }} />
                            )}
                          </Box>
                          {deal.trustScore && (
                            <Typography variant="caption" color="text.secondary">
                              Trust Score: {deal.trustScore}/100
                            </Typography>
                          )}
                        </Box>
                        <Box
                          sx={{
                            minWidth: 48,
                            height: 48,
                            borderRadius: 2,
                            backgroundColor: `${scoreColor}15`,
                            border: `1px solid ${scoreColor}40`,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                          }}
                        >
                          <Typography sx={{ fontWeight: 800, color: scoreColor, fontSize: '1.1rem' }}>
                            {deal.dealScore}
                          </Typography>
                        </Box>
                      </Box>

                      {/* Deal Score Bar */}
                      <Box sx={{ mb: 2.5 }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                          <Typography variant="caption" color="text.secondary">Deal Score</Typography>
                          <Typography variant="caption" sx={{ color: scoreColor, fontWeight: 700 }}>
                            {deal.dealScore}/100
                          </Typography>
                        </Box>
                        <LinearProgress
                          variant="determinate"
                          value={deal.dealScore}
                          sx={{
                            height: 6,
                            borderRadius: 3,
                            backgroundColor: 'rgba(255,255,255,0.06)',
                            '& .MuiLinearProgress-bar': {
                              borderRadius: 3,
                              background: `linear-gradient(90deg, ${scoreColor}, ${scoreColor}aa)`,
                            },
                          }}
                        />
                      </Box>

                      {/* Price */}
                      <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 1.5, mb: 1 }}>
                        <Typography
                          variant="h5"
                          sx={{ fontWeight: 800, color: hasSavings ? 'primary.main' : 'text.primary' }}
                        >
                          ${deal.currentPrice?.toFixed(2)}
                        </Typography>
                        {hasSavings && deal.marketAverage && (
                          <Typography
                            variant="body2"
                            sx={{ color: 'text.secondary', textDecoration: 'line-through' }}
                          >
                            ${deal.marketAverage.toFixed(2)}
                          </Typography>
                        )}
                      </Box>

                      {/* Chips: Savings + Trend */}
                      <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                        {hasSavings && (
                          <Chip
                            label={`Save ${deal.savingsPercentage.toFixed(0)}%`}
                            size="small"
                            sx={{
                              backgroundColor: 'rgba(0,230,118,0.12)',
                              color: '#00e676',
                              fontWeight: 700,
                              border: '1px solid rgba(0,230,118,0.25)',
                            }}
                          />
                        )}
                        <Chip
                          icon={<TrendIcon trend={deal.priceTrend} />}
                          label={deal.priceTrend === 'DOWN' ? 'Prices Falling' : deal.priceTrend === 'UP' ? 'Prices Rising' : 'Stable'}
                          size="small"
                          sx={{
                            backgroundColor: 'rgba(255,255,255,0.04)',
                            color: 'text.secondary',
                            fontWeight: 600,
                            border: '1px solid rgba(255,255,255,0.08)',
                          }}
                        />
                      </Box>

                      {/* Recommendation */}
                      {deal.recommendation && (
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 2, fontSize: '0.8rem', lineHeight: 1.5, flexGrow: 1 }}>
                          {deal.recommendation}
                        </Typography>
                      )}

                      {/* Actions */}
                      <Box sx={{ display: 'flex', gap: 1.5, mt: 'auto' }}>
                        <Button
                          variant="contained"
                          size="small"
                          component={RouterLink}
                          to={`/matches/${deal.matchId}`}
                          startIcon={<SportsSoccerIcon />}
                          sx={{ flex: 1 }}
                        >
                          View Match
                        </Button>
                        {deal.bookingUrl && (
                          <Button
                            variant="outlined"
                            size="small"
                            href={deal.bookingUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            sx={{
                              flex: 1,
                              borderColor: 'rgba(255,193,7,0.4)',
                              color: '#ffc107',
                              '&:hover': {
                                borderColor: '#ffc107',
                                backgroundColor: 'rgba(255,193,7,0.06)',
                              },
                            }}
                          >
                            Book Now
                          </Button>
                        )}
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        )}
      </Container>
    </Box>
  );
};

export default DealsPage;

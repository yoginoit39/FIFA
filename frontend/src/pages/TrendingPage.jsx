import { Container, Typography, Box, Grid, Card, CardContent, Chip, Button, LinearProgress } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useMarketOverview, useTrendingMatches, usePriceDrops } from '../hooks/useDeals';
import { useMatchLookup } from '../hooks/useMatches';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingFlatIcon from '@mui/icons-material/TrendingFlat';
import BarChartIcon from '@mui/icons-material/BarChart';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import WhatshotIcon from '@mui/icons-material/Whatshot';
import SavingsIcon from '@mui/icons-material/Savings';
import StorefrontIcon from '@mui/icons-material/Storefront';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

const TrendIcon = ({ trend, size = 20 }) => {
  if (trend === 'DOWN') return <TrendingDownIcon sx={{ color: '#00e676', fontSize: size }} />;
  if (trend === 'UP') return <TrendingUpIcon sx={{ color: '#ff5252', fontSize: size }} />;
  return <TrendingFlatIcon sx={{ color: '#94a3b8', fontSize: size }} />;
};

const StatCard = ({ icon, value, label, color = '#00e676', subtext }) => (
  <Card
    sx={{
      background: 'linear-gradient(160deg, #111d2e 0%, #0f1924 40%)',
      border: '1px solid rgba(255,255,255,0.06)',
      height: '100%',
    }}
  >
    <CardContent sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: 2,
            backgroundColor: `${color}12`,
            border: `1px solid ${color}30`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {icon}
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 600 }}>
          {label}
        </Typography>
      </Box>
      <Typography
        variant="h4"
        sx={{
          fontWeight: 800,
          background: `linear-gradient(135deg, ${color}, ${color}aa)`,
          backgroundClip: 'text',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          lineHeight: 1.1,
        }}
      >
        {value}
      </Typography>
      {subtext && (
        <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
          {subtext}
        </Typography>
      )}
    </CardContent>
  </Card>
);

const getPopularityColor = (score) => {
  if (score >= 70) return '#00e676';
  if (score >= 40) return '#ffc107';
  return '#64b5f6';
};

const TrendingPage = () => {
  const { data: overview, isLoading: overviewLoading, error: overviewError, refetch: refetchOverview } = useMarketOverview();
  const { data: trending, isLoading: trendingLoading } = useTrendingMatches(18);
  const { data: priceDrops, isLoading: dropsLoading } = usePriceDrops(10);
  const { lookup: matchLookup } = useMatchLookup();

  const getMatchLabel = (matchId) => {
    const m = matchLookup[matchId];
    if (m) return `${m.homeTeam?.name || 'TBD'} vs ${m.awayTeam?.name || 'TBD'}`;
    return `Match ${matchId}`;
  };

  const isLoading = overviewLoading && trendingLoading;

  return (
    <Box>
      {/* Hero Banner */}
      <Box
        sx={{
          position: 'relative',
          overflow: 'hidden',
          py: { xs: 8, md: 12 },
          textAlign: 'center',
          background: 'linear-gradient(180deg, #060b14 0%, #0d1520 40%, #100d18 75%, #060b14 100%)',
          '&::before': {
            content: '""',
            position: 'absolute',
            inset: 0,
            background: 'radial-gradient(ellipse 70% 60% at 50% 0%, rgba(100,181,246,0.06) 0%, transparent 70%)',
            pointerEvents: 'none',
          },
          '&::after': {
            content: '""',
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: '1px',
            background: 'linear-gradient(90deg, transparent, rgba(100,181,246,0.2), transparent)',
          },
        }}
      >
        <Container maxWidth="md" sx={{ position: 'relative' }}>
          <Box
            sx={{
              width: 56,
              height: 56,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #64b5f6, #1976d2)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              mx: 'auto',
              mb: 3,
              boxShadow: '0 0 24px rgba(100,181,246,0.3)',
            }}
          >
            <BarChartIcon sx={{ color: '#fff', fontSize: 28 }} />
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
            Market Analytics
          </Typography>
          <Typography
            variant="h5"
            sx={{ color: 'text.secondary', mb: 2, fontWeight: 400, fontSize: { xs: '1rem', md: '1.15rem' } }}
          >
            Real-time trends, price movements, and market insights for FIFA World Cup 2026 tickets
          </Typography>
        </Container>
      </Box>

      <Container maxWidth={false} sx={{ py: { xs: 6, md: 10 }, px: { xs: 2, sm: 4, md: 6, lg: 8 } }}>
        {/* Market Overview */}
        <Box sx={{ mb: 8 }}>
          <Box sx={{ mb: 4 }}>
            <Typography variant="overline" sx={{ color: '#64b5f6', fontWeight: 700, letterSpacing: '0.1em' }}>
              Overview
            </Typography>
            <Typography variant="h4" sx={{ mt: 0.5 }}>
              Market Snapshot
            </Typography>
          </Box>

          {overviewLoading ? (
            <Loading message="Loading market data..." />
          ) : overviewError ? (
            <ErrorMessage message="Could not load market overview. The analytics service may still be initializing." onRetry={refetchOverview} />
          ) : overview ? (
            <Grid container spacing={3}>
              <Grid item xs={6} sm={4} lg={2}>
                <StatCard
                  icon={<SportsSoccerIcon sx={{ color: '#64b5f6', fontSize: 20 }} />}
                  value={overview.totalMatches}
                  label="Matches Tracked"
                  color="#64b5f6"
                />
              </Grid>
              <Grid item xs={6} sm={4} lg={2}>
                <StatCard
                  icon={<StorefrontIcon sx={{ color: '#ffc107', fontSize: 20 }} />}
                  value={overview.totalProviders}
                  label="Active Providers"
                  color="#ffc107"
                />
              </Grid>
              <Grid item xs={6} sm={4} lg={2}>
                <StatCard
                  icon={<WhatshotIcon sx={{ color: '#ff5252', fontSize: 20 }} />}
                  value={overview.hotDealCount}
                  label="Hot Deals"
                  color="#ff5252"
                  subtext="Score 70+"
                />
              </Grid>
              <Grid item xs={6} sm={4} lg={2}>
                <StatCard
                  icon={<SavingsIcon sx={{ color: '#00e676', fontSize: 20 }} />}
                  value={`$${overview.overallLowestPrice?.toFixed(0) || '—'}`}
                  label="Lowest Price"
                  color="#00e676"
                />
              </Grid>
              <Grid item xs={6} sm={4} lg={2}>
                <StatCard
                  icon={<LocalOfferIcon sx={{ color: '#ce93d8', fontSize: 20 }} />}
                  value={`$${overview.averagePrice?.toFixed(0) || '—'}`}
                  label="Avg. Price"
                  color="#ce93d8"
                />
              </Grid>
              <Grid item xs={6} sm={4} lg={2}>
                <StatCard
                  icon={<BarChartIcon sx={{ color: '#64b5f6', fontSize: 20 }} />}
                  value={overview.averageDealScore}
                  label="Avg. Deal Score"
                  color="#64b5f6"
                  subtext={`${overview.totalDeals} total deals`}
                />
              </Grid>
            </Grid>
          ) : null}

          {/* Trend Direction Summary */}
          {overview && (
            <Box
              sx={{
                mt: 3,
                p: 3,
                borderRadius: 3,
                background: 'linear-gradient(160deg, #111d2e 0%, #0f1924 40%)',
                border: '1px solid rgba(255,255,255,0.06)',
                display: 'flex',
                gap: { xs: 2, md: 4 },
                flexWrap: 'wrap',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <TrendingDownIcon sx={{ color: '#00e676', fontSize: 22 }} />
                <Typography variant="body2" color="text.secondary">
                  <Box component="span" sx={{ color: '#00e676', fontWeight: 700 }}>{overview.pricesDownCount}</Box> prices dropping
                </Typography>
              </Box>
              <Box sx={{ width: '1px', height: 20, backgroundColor: 'rgba(255,255,255,0.1)', display: { xs: 'none', sm: 'block' } }} />
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <TrendingUpIcon sx={{ color: '#ff5252', fontSize: 22 }} />
                <Typography variant="body2" color="text.secondary">
                  <Box component="span" sx={{ color: '#ff5252', fontWeight: 700 }}>{overview.pricesUpCount}</Box> prices rising
                </Typography>
              </Box>
              <Box sx={{ width: '1px', height: 20, backgroundColor: 'rgba(255,255,255,0.1)', display: { xs: 'none', sm: 'block' } }} />
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <TrendingFlatIcon sx={{ color: '#94a3b8', fontSize: 22 }} />
                <Typography variant="body2" color="text.secondary">
                  <Box component="span" sx={{ color: '#94a3b8', fontWeight: 700 }}>{overview.pricesStableCount}</Box> stable
                </Typography>
              </Box>
              <Box sx={{ width: '1px', height: 20, backgroundColor: 'rgba(255,255,255,0.1)', display: { xs: 'none', sm: 'block' } }} />
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography variant="body2" color="text.secondary">
                  <Box component="span" sx={{ color: '#ffc107', fontWeight: 700 }}>{overview.buyNowPercentage}%</Box> recommend buy now
                </Typography>
              </Box>
            </Box>
          )}
        </Box>

        {/* Trending Matches */}
        <Box sx={{ mb: 8 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', mb: 4 }}>
            <Box>
              <Typography variant="overline" sx={{ color: '#ff5252', fontWeight: 700, letterSpacing: '0.1em' }}>
                Trending
              </Typography>
              <Typography variant="h4" sx={{ mt: 0.5 }}>
                Most Popular Matches
              </Typography>
            </Box>
          </Box>

          {trendingLoading ? (
            <Loading message="Computing trends..." />
          ) : !trending || trending.length === 0 ? (
            <Box sx={{ textAlign: 'center', py: 8, border: '1px solid rgba(255,255,255,0.06)', borderRadius: 3 }}>
              <BarChartIcon sx={{ fontSize: 56, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" color="text.secondary">No trending data available yet</Typography>
            </Box>
          ) : (
            <Grid container spacing={2}>
              {trending.map((match) => {
                const popColor = getPopularityColor(match.popularityScore);
                const isTop3 = match.rank <= 3;
                return (
                  <Grid item xs={12} sm={6} lg={4} key={match.matchId}>
                    <Card
                      sx={{
                        height: '100%',
                        background: isTop3
                          ? `linear-gradient(160deg, ${popColor}08 0%, #0f1924 40%)`
                          : 'linear-gradient(160deg, #111d2e 0%, #0f1924 40%)',
                        border: '1px solid',
                        borderColor: isTop3 ? `${popColor}30` : 'rgba(255,255,255,0.06)',
                        transition: 'all 0.3s ease',
                        '&:hover': {
                          transform: 'translateY(-4px)',
                          boxShadow: `0 8px 30px ${isTop3 ? `${popColor}15` : 'rgba(0,0,0,0.3)'}`,
                          borderColor: isTop3 ? `${popColor}50` : 'rgba(255,255,255,0.12)',
                        },
                      }}
                    >
                      <CardContent sx={{ p: 3 }}>
                        {/* Header: Rank + Match ID + Popularity */}
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                            <Box
                              sx={{
                                width: 36,
                                height: 36,
                                borderRadius: '50%',
                                backgroundColor: isTop3 ? `${popColor}18` : 'rgba(255,255,255,0.06)',
                                border: `1px solid ${isTop3 ? `${popColor}40` : 'rgba(255,255,255,0.1)'}`,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                              }}
                            >
                              <Typography sx={{ fontWeight: 800, color: isTop3 ? popColor : 'text.secondary', fontSize: '0.9rem' }}>
                                #{match.rank}
                              </Typography>
                            </Box>
                            <Box>
                              <Typography variant="h6" sx={{ fontWeight: 700, lineHeight: 1.2 }}>
                                {getMatchLabel(match.matchId)}
                              </Typography>
                              {match.bestProviderName && (
                                <Typography variant="caption" color="text.secondary">
                                  Best: {match.bestProviderName}
                                </Typography>
                              )}
                            </Box>
                          </Box>
                          <TrendIcon trend={match.priceTrend} size={22} />
                        </Box>

                        {/* Popularity Bar */}
                        <Box sx={{ mb: 2 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                            <Typography variant="caption" color="text.secondary">Popularity</Typography>
                            <Typography variant="caption" sx={{ color: popColor, fontWeight: 700 }}>
                              {match.popularityScore}/100
                            </Typography>
                          </Box>
                          <LinearProgress
                            variant="determinate"
                            value={match.popularityScore}
                            sx={{
                              height: 5,
                              borderRadius: 3,
                              backgroundColor: 'rgba(255,255,255,0.06)',
                              '& .MuiLinearProgress-bar': {
                                borderRadius: 3,
                                background: `linear-gradient(90deg, ${popColor}, ${popColor}88)`,
                              },
                            }}
                          />
                        </Box>

                        {/* Price + Deal Score */}
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', mb: 1.5 }}>
                          <Typography variant="h6" sx={{ fontWeight: 800 }}>
                            ${match.lowestPrice?.toFixed(2)}
                          </Typography>
                          <Chip
                            label={`Score: ${match.bestDealScore}`}
                            size="small"
                            sx={{
                              backgroundColor: `${getPopularityColor(match.bestDealScore)}12`,
                              color: getPopularityColor(match.bestDealScore),
                              fontWeight: 700,
                              border: `1px solid ${getPopularityColor(match.bestDealScore)}30`,
                              fontSize: '0.7rem',
                            }}
                          />
                        </Box>

                        {/* Chips */}
                        <Box sx={{ display: 'flex', gap: 0.75, mb: 2, flexWrap: 'wrap' }}>
                          {match.maxSavingsPercentage > 0 && (
                            <Chip
                              label={`Save ${match.maxSavingsPercentage.toFixed(0)}%`}
                              size="small"
                              sx={{
                                backgroundColor: 'rgba(0,230,118,0.1)',
                                color: '#00e676',
                                fontWeight: 700,
                                border: '1px solid rgba(0,230,118,0.25)',
                                fontSize: '0.7rem',
                              }}
                            />
                          )}
                          <Chip
                            label={`${match.numProviders} providers`}
                            size="small"
                            sx={{
                              backgroundColor: 'rgba(255,255,255,0.04)',
                              color: 'text.secondary',
                              fontWeight: 600,
                              border: '1px solid rgba(255,255,255,0.08)',
                              fontSize: '0.7rem',
                            }}
                          />
                          {match.bestTimeToBuy === 'NOW' && (
                            <Chip
                              label="Buy Now"
                              size="small"
                              sx={{
                                backgroundColor: 'rgba(255,193,7,0.1)',
                                color: '#ffc107',
                                fontWeight: 700,
                                border: '1px solid rgba(255,193,7,0.25)',
                                fontSize: '0.7rem',
                              }}
                            />
                          )}
                        </Box>

                        {/* Reason */}
                        <Typography variant="body2" color="text.secondary" sx={{ fontSize: '0.78rem', lineHeight: 1.5, mb: 2 }}>
                          {match.trendingReason}
                        </Typography>

                        {/* Action */}
                        <Button
                          variant="outlined"
                          size="small"
                          fullWidth
                          component={RouterLink}
                          to={`/matches/${match.matchId}`}
                          endIcon={<ArrowForwardIcon sx={{ fontSize: 16 }} />}
                          sx={{
                            borderColor: `${popColor}30`,
                            color: popColor,
                            fontWeight: 600,
                            '&:hover': {
                              borderColor: popColor,
                              backgroundColor: `${popColor}08`,
                            },
                          }}
                        >
                          View Match Details
                        </Button>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          )}
        </Box>

        {/* Biggest Price Drops */}
        <Box sx={{ mb: 4 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', mb: 4 }}>
            <Box>
              <Typography variant="overline" sx={{ color: '#00e676', fontWeight: 700, letterSpacing: '0.1em' }}>
                Savings
              </Typography>
              <Typography variant="h4" sx={{ mt: 0.5 }}>
                Biggest Price Drops
              </Typography>
            </Box>
            <Button
              component={RouterLink}
              to="/deals"
              endIcon={<LocalOfferIcon />}
              sx={{ color: 'secondary.main', fontWeight: 600 }}
            >
              All Deals
            </Button>
          </Box>

          {dropsLoading ? (
            <Loading message="Finding best savings..." />
          ) : !priceDrops || priceDrops.length === 0 ? (
            <Box sx={{ textAlign: 'center', py: 6, border: '1px solid rgba(255,255,255,0.06)', borderRadius: 3 }}>
              <Typography variant="h6" color="text.secondary">No price drops detected yet</Typography>
            </Box>
          ) : (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
              {priceDrops.map((drop) => (
                <Card
                  key={drop.matchId}
                  component={RouterLink}
                  to={`/matches/${drop.matchId}`}
                  sx={{
                    textDecoration: 'none',
                    background: 'linear-gradient(160deg, #111d2e 0%, #0f1924 40%)',
                    border: '1px solid rgba(255,255,255,0.06)',
                    transition: 'all 0.2s ease',
                    '&:hover': {
                      borderColor: 'rgba(0,230,118,0.2)',
                      backgroundColor: 'rgba(0,230,118,0.02)',
                    },
                  }}
                >
                  <CardContent sx={{ p: 2.5, '&:last-child': { pb: 2.5 } }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}>
                      {/* Rank + Match */}
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, minWidth: 0 }}>
                        <Box
                          sx={{
                            width: 32,
                            height: 32,
                            borderRadius: '50%',
                            backgroundColor: 'rgba(0,230,118,0.1)',
                            border: '1px solid rgba(0,230,118,0.25)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            flexShrink: 0,
                          }}
                        >
                          <Typography sx={{ fontWeight: 800, color: '#00e676', fontSize: '0.8rem' }}>
                            #{drop.rank}
                          </Typography>
                        </Box>
                        <Box sx={{ minWidth: 0 }}>
                          <Typography variant="body1" sx={{ fontWeight: 700 }}>
                            {getMatchLabel(drop.matchId)}
                          </Typography>
                          <Typography variant="caption" color="text.secondary" noWrap>
                            {drop.trendingReason}
                          </Typography>
                        </Box>
                      </Box>

                      {/* Price + Savings */}
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexShrink: 0 }}>
                        <Box sx={{ textAlign: 'right' }}>
                          <Typography variant="body1" sx={{ fontWeight: 800 }}>
                            ${drop.lowestPrice?.toFixed(2)}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            avg ${drop.averagePrice?.toFixed(0)}
                          </Typography>
                        </Box>
                        <Chip
                          label={`-${drop.maxSavingsPercentage?.toFixed(0)}%`}
                          size="small"
                          sx={{
                            backgroundColor: 'rgba(0,230,118,0.12)',
                            color: '#00e676',
                            fontWeight: 800,
                            border: '1px solid rgba(0,230,118,0.3)',
                            minWidth: 60,
                          }}
                        />
                        <TrendIcon trend={drop.priceTrend} />
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      </Container>
    </Box>
  );
};

export default TrendingPage;

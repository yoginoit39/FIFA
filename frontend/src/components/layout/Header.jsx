import { AppBar, Toolbar, Typography, Button, Box, Container } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import StadiumIcon from '@mui/icons-material/Stadium';
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';

const Header = () => {
  const location = useLocation();

  const navLinks = [
    { label: 'Matches', path: '/matches', icon: <SportsSoccerIcon fontSize="small" /> },
    { label: 'Stadiums', path: '/stadiums', icon: <StadiumIcon fontSize="small" /> },
    { label: 'Deals', path: '/deals', icon: <LocalOfferIcon fontSize="small" /> },
  ];

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        backgroundColor: 'rgba(6, 11, 20, 0.92)',
        backdropFilter: 'blur(20px)',
        borderBottom: '1px solid rgba(255,255,255,0.06)',
      }}
    >
      <Container maxWidth="xl">
        <Toolbar disableGutters sx={{ gap: 2, py: 0.5 }}>
          {/* Logo */}
          <Box
            component={RouterLink}
            to="/"
            sx={{
              display: 'flex',
              alignItems: 'center',
              gap: 1.5,
              textDecoration: 'none',
              mr: 4,
            }}
          >
            <Box
              sx={{
                width: 40,
                height: 40,
                borderRadius: '50%',
                background: 'linear-gradient(135deg, #00e676, #00b248)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 0 16px rgba(0,230,118,0.3)',
              }}
            >
              <SportsSoccerIcon sx={{ color: '#000', fontSize: 22 }} />
            </Box>
            <Box>
              <Typography
                variant="h6"
                sx={{
                  fontWeight: 800,
                  color: 'white',
                  lineHeight: 1.1,
                  letterSpacing: '-0.02em',
                  fontSize: '1rem',
                }}
              >
                FIFA World Cup
              </Typography>
              <Typography variant="caption" sx={{ color: 'primary.main', fontWeight: 700, letterSpacing: '0.05em' }}>
                2026
              </Typography>
            </Box>
          </Box>

          {/* Nav Links */}
          <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 0.5, flexGrow: 1 }}>
            {navLinks.map((link) => {
              const active = location.pathname.startsWith(link.path);
              return (
                <Button
                  key={link.path}
                  component={RouterLink}
                  to={link.path}
                  startIcon={link.icon}
                  sx={{
                    color: active ? 'primary.main' : 'text.secondary',
                    backgroundColor: active ? 'rgba(0,230,118,0.06)' : 'transparent',
                    '&:hover': {
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.06)',
                    },
                    borderRadius: 2,
                    px: 2,
                    py: 1,
                    fontWeight: 600,
                  }}
                >
                  {link.label}
                </Button>
              );
            })}
          </Box>

          {/* CTA Button */}
          <Button
            variant="contained"
            component={RouterLink}
            to="/matches"
            startIcon={<ConfirmationNumberIcon />}
            sx={{
              background: 'linear-gradient(135deg, #ffc107, #ff8f00)',
              color: '#000',
              fontWeight: 700,
              '&:hover': {
                background: 'linear-gradient(135deg, #ffd54f, #ffc107)',
                boxShadow: '0 0 20px rgba(255,193,7,0.3)',
              },
            }}
          >
            Get Tickets
          </Button>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Header;

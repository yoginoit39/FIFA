import { Box, Container, Typography } from '@mui/material';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';

const Footer = () => {
  return (
    <Box
      component="footer"
      sx={{
        py: 4,
        borderTop: '1px solid rgba(255,255,255,0.06)',
        backgroundColor: '#060b14',
      }}
    >
      <Container maxWidth="xl">
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <SportsSoccerIcon sx={{ color: 'primary.main', fontSize: 20 }} />
            <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 500 }}>
              FIFA World Cup 2026 · USA · Canada · Mexico
            </Typography>
          </Box>
          <Box sx={{ display: 'flex', gap: 3 }}>
            <Typography variant="caption" color="text.secondary">Built with Spring Boot & React</Typography>
            <Typography variant="caption" color="text.secondary">For educational purposes only</Typography>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default Footer;

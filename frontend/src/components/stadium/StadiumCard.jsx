/**
 * Stadium Card Component
 * Displays a single stadium in card format
 */
import { Card, CardContent, CardActions, Typography, Box, Button, Chip } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import PlaceIcon from '@mui/icons-material/Place';
import PeopleIcon from '@mui/icons-material/People';

const StadiumCard = ({ stadium }) => {
  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          {stadium.name}
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 1 }}>
          <PlaceIcon fontSize="small" color="action" />
          <Typography variant="body1" color="text.secondary">
            {stadium.city}, {stadium.state || stadium.country}
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 2 }}>
          <PeopleIcon fontSize="small" color="action" />
          <Typography variant="body2" color="text.secondary">
            Capacity: {stadium.capacity?.toLocaleString() || 'N/A'}
          </Typography>
        </Box>

        {stadium.description && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
            {stadium.description.length > 150
              ? `${stadium.description.substring(0, 150)}...`
              : stadium.description}
          </Typography>
        )}

        <Box sx={{ mt: 2 }}>
          <Chip
            label={stadium.country}
            color="primary"
            size="small"
            variant="outlined"
          />
        </Box>
      </CardContent>

      <CardActions>
        <Button
          component={RouterLink}
          to={`/stadiums/${stadium.id}`}
          size="small"
          fullWidth
          variant="outlined"
        >
          View Details & Matches
        </Button>
      </CardActions>
    </Card>
  );
};

export default StadiumCard;

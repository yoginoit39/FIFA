# FIFA World Cup 2026 - React Frontend

Modern React frontend for the FIFA World Cup 2026 application, displaying matches, stadiums, and ticket booking information.

## Technology Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router v6** - Client-side routing
- **React Query (TanStack Query)** - Server state management and caching
- **Material-UI (MUI)** - Component library
- **Axios** - HTTP client
- **date-fns** - Date formatting utilities

## Project Structure

```
frontend/
├── public/                 # Static assets
├── src/
│   ├── components/         # Reusable components
│   │   ├── common/         # Common components (Loading, ErrorMessage)
│   │   ├── layout/         # Layout components (Header, Footer)
│   │   ├── match/          # Match-related components (MatchCard)
│   │   └── stadium/        # Stadium-related components (StadiumCard)
│   ├── hooks/              # React Query hooks
│   │   ├── useMatches.js   # Match data hooks
│   │   ├── useStadiums.js  # Stadium data hooks
│   │   └── useTickets.js   # Ticket data hooks
│   ├── pages/              # Page components
│   │   ├── HomePage.jsx
│   │   ├── MatchesPage.jsx
│   │   ├── MatchDetailsPage.jsx
│   │   ├── StadiumsPage.jsx
│   │   └── StadiumDetailsPage.jsx
│   ├── services/           # API service layer
│   │   ├── api.js          # Axios instance
│   │   ├── matchService.js
│   │   ├── stadiumService.js
│   │   └── ticketService.js
│   ├── utils/              # Utility functions
│   │   └── dateFormatter.js
│   ├── App.jsx             # Main app component
│   └── main.jsx            # Entry point
├── Dockerfile              # Production build (Nginx)
├── Dockerfile.dev          # Development build (hot reload)
├── nginx.conf              # Nginx configuration for production
├── vite.config.js          # Vite configuration
└── package.json            # Dependencies
```

## Features

### Pages
- **Home Page** - Hero section with featured upcoming matches
- **Matches Page** - List all matches with tabs (Upcoming/Live)
- **Match Details** - Detailed match view with teams, venue, and ticket links
- **Stadiums Page** - List all 16 FIFA 2026 stadiums with country filter
- **Stadium Details** - Stadium information with scheduled matches

### Components
- **MatchCard** - Displays match information in card format
- **StadiumCard** - Displays stadium information in card format
- **Loading** - Loading spinner with customizable message
- **ErrorMessage** - Error display with retry button
- **Header** - Navigation bar with links
- **Footer** - Copyright and project info

### API Integration
- Axios instance with request/response interceptors
- Global error handling
- 10-second request timeout
- Environment-based API URL configuration

### Caching Strategy
- React Query with configurable stale times:
  - Matches: 30 minutes
  - Teams: 60 minutes
  - Stadiums: 60 minutes
  - Tickets: 30 minutes
- Automatic refetching on window focus disabled
- Retry on failure: 1 attempt

## Environment Variables

Create `.env.development` for local development:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Development Setup

### Prerequisites
- Node.js 18+
- npm or yarn

### Install Dependencies
```bash
npm install
```

### Run Development Server
```bash
npm run dev
```

Frontend will be available at http://localhost:5173

### Build for Production
```bash
npm run build
```

Production build will be in `dist/` folder.

## Docker Development

### Build Development Image
```bash
docker build -f Dockerfile.dev -t worldcup2026-frontend:dev .
```

### Run with Docker Compose
```bash
cd ..
docker-compose up frontend
```

## API Endpoints Used

### Match Service (via API Gateway)
- `GET /api/matches` - List all matches
- `GET /api/matches/{id}` - Get match details
- `GET /api/matches/upcoming` - Get upcoming matches
- `GET /api/matches/live` - Get live matches
- `GET /api/teams` - List all teams

### Stadium Service (via API Gateway)
- `GET /api/stadiums` - List all stadiums
- `GET /api/stadiums/{id}` - Get stadium details
- `GET /api/stadiums/{id}/matches` - Get matches at stadium

### Ticket Service (via API Gateway)
- `GET /api/tickets/match/{matchId}` - Get ticket links for match

## Troubleshooting

### Port 5173 already in use
```bash
lsof -ti:5173 | xargs kill -9
```

### Hot reload not working in Docker
- Ensure `usePolling: true` is set in vite.config.js
- Verify volume mounts in docker-compose.yml

### API calls failing (CORS errors)
- Check API Gateway CORS configuration
- Verify `VITE_API_BASE_URL` environment variable
- Ensure API Gateway is running at http://localhost:8080

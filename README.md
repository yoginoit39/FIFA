# FIFA World Cup 2026

A full-stack microservices application for browsing FIFA World Cup 2026 match schedules, stadiums, and comparing ticket prices across providers.

## Tech Stack

- **Backend**: Spring Boot 4.0.2, Java 17, PostgreSQL 16
- **Frontend**: React 18, Vite, Material UI, React Query
- **Infrastructure**: Docker, Docker Compose

## Architecture

```
├── backend/
│   ├── api-gateway/        # Routing & CORS (port 8080)
│   ├── match-service/      # Matches & teams (port 8081)
│   ├── stadium-service/    # Stadiums (port 8082)
│   └── ticket-service/     # Ticket links (port 8083)
├── frontend/               # React app (port 5173)
├── database/               # Init scripts & seed data
└── docker-compose.yml
```

## Quick Start

**Requirements:** Docker Desktop

```bash
git clone https://github.com/YOUR_USERNAME/fifa-worldcup-2026.git
cd fifa-worldcup-2026
docker compose up --build
```

Then open http://localhost:5173

## Features

- Browse all 48 World Cup matches with live scores
- Filter matches by country and city
- View match details with team flags and venue info
- Compare ticket prices across FIFA Official, Ticketmaster, StubHub, and SeatGeek
- Responsive dark-themed UI

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/matches` | All matches (paginated) |
| GET | `/api/matches/upcoming` | Upcoming matches |
| GET | `/api/matches/live` | Live matches |
| GET | `/api/matches/{id}` | Match details |
| GET | `/api/stadiums` | All stadiums |
| GET | `/api/stadiums/{id}` | Stadium details |
| GET | `/api/tickets/match/{id}` | Ticket links for match |

## License

For educational purposes only. Not affiliated with FIFA.

# FIFA World Cup 2026

A full-stack microservices application for browsing FIFA World Cup 2026 match schedules, stadiums, and comparing ticket prices across providers.

## Tech Stack

- **Backend**: Spring Boot, Java 17, PostgreSQL 16
- **Frontend**: React 19, Vite, Material UI, React Query
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

## Quick Start (Local)

**Requirements:** Docker Desktop

```bash
git clone https://github.com/YOUR_USERNAME/fifa-worldcup-2026.git
cd fifa-worldcup-2026
docker compose up --build
```

Then open http://localhost:5173

## Features

- Browse World Cup matches with live scores
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

## Free Hosting Deployment

This app can be hosted for free using **Render.com** (backend + database) and **Vercel** (frontend).

### Step 1 — Deploy Backend on Render.com

1. Go to [render.com](https://render.com) and create a free account
2. Click **New → Blueprint** and connect your GitHub repository
3. Render will detect the `render.yaml` file and create all services automatically:
   - `worldcup-db` — PostgreSQL database
   - `match-service` — Match & team data
   - `stadium-service` — Stadium data
   - `ticket-service` — Ticket links
   - `api-gateway` — Routes all requests

4. Once deployed, **initialize the database schema**:
   - In Render dashboard, go to `worldcup-db` → **Connect** tab
   - Copy the **External Database URL**
   - Run the init script:
     ```bash
     psql "YOUR_RENDER_DB_URL" -f database/init/01-create-schemas.sql
     ```

5. Note your **api-gateway** service URL (e.g., `https://api-gateway-xxxx.onrender.com`)

### Step 2 — Deploy Frontend on Vercel

1. Go to [vercel.com](https://vercel.com) and create a free account
2. Click **New Project** → Import your GitHub repository
3. Set the **Root Directory** to `frontend`
4. Add environment variable:
   - `VITE_API_BASE_URL` = `https://your-api-gateway-url.onrender.com`
5. Click **Deploy**

### Notes

- Render free tier services spin down after 15 min of inactivity — first request may be slow (~30s)
- Free PostgreSQL on Render has 256MB storage limit
- Vercel free tier supports unlimited deployments

## License

For educational purposes only. Not affiliated with FIFA.

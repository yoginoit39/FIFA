#!/bin/bash

# FIFA World Cup 2026 - Status Check Script
# Checks if all services are running and healthy

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}FIFA World Cup 2026 - Service Status${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

check_service() {
    local name=$1
    local port=$2
    local health_url=$3

    echo -n "[$name] Port $port: "

    # Check if port is in use
    if ! lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${RED}✗ NOT RUNNING${NC}"
        return 1
    fi

    # Check health endpoint
    if [ ! -z "$health_url" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" "$health_url" 2>&1)
        if [ "$response" = "200" ]; then
            echo -e "${GREEN}✓ RUNNING & HEALTHY${NC}"
            return 0
        else
            echo -e "${YELLOW}⚠ RUNNING but unhealthy (HTTP $response)${NC}"
            return 1
        fi
    else
        echo -e "${GREEN}✓ RUNNING${NC}"
        return 0
    fi
}

# Check PostgreSQL
echo -e "${BLUE}Database:${NC}"
if docker ps | grep -q worldcup2026-postgres; then
    echo -e "[PostgreSQL] ${GREEN}✓ RUNNING${NC}"
else
    echo -e "[PostgreSQL] ${RED}✗ NOT RUNNING${NC}"
fi
echo ""

# Check Backend Services
echo -e "${BLUE}Backend Services:${NC}"
check_service "Match Service  " 8081 "http://localhost:8081/actuator/health"
check_service "Stadium Service" 8082 "http://localhost:8082/actuator/health"
check_service "Ticket Service " 8083 "http://localhost:8083/actuator/health"
check_service "API Gateway    " 8080 ""
echo ""

# Check Frontend
echo -e "${BLUE}Frontend:${NC}"
check_service "React App      " 5173 "http://localhost:5173"
echo ""

# Test API endpoints
echo -e "${BLUE}API Endpoints:${NC}"

test_api() {
    local name=$1
    local url=$2

    echo -n "[$name] "
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>&1)

    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✓ OK (HTTP $response)${NC}"
        return 0
    else
        echo -e "${RED}✗ FAIL (HTTP $response)${NC}"
        return 1
    fi
}

test_api "GET /api/matches       " "http://localhost:8080/api/matches"
test_api "GET /api/stadiums      " "http://localhost:8080/api/stadiums"
test_api "GET /api/tickets       " "http://localhost:8080/api/tickets"
test_api "GET /api/matches/upcoming" "http://localhost:8080/api/matches/upcoming"

echo ""
echo -e "${BLUE}============================================${NC}"
echo ""

# Check data in database
echo -e "${BLUE}Database Content:${NC}"
if docker ps | grep -q worldcup2026-postgres; then
    stadium_count=$(docker exec worldcup2026-postgres psql -U worldcup_user -d worldcup2026_db -tAc "SELECT COUNT(*) FROM stadium_service_schema.stadiums;" 2>/dev/null)
    team_count=$(docker exec worldcup2026-postgres psql -U worldcup_user -d worldcup2026_db -tAc "SELECT COUNT(*) FROM match_service_schema.teams;" 2>/dev/null)
    match_count=$(docker exec worldcup2026-postgres psql -U worldcup_user -d worldcup2026_db -tAc "SELECT COUNT(*) FROM match_service_schema.matches;" 2>/dev/null)

    echo "  Stadiums: ${GREEN}$stadium_count${NC}"
    echo "  Teams:    ${GREEN}$team_count${NC}"
    echo "  Matches:  ${GREEN}$match_count${NC}"
else
    echo -e "  ${RED}PostgreSQL not running${NC}"
fi

echo ""
echo -e "${BLUE}Access URLs:${NC}"
echo "  Frontend:    ${GREEN}http://localhost:5173${NC}"
echo "  API Gateway: ${GREEN}http://localhost:8080${NC}"
echo ""

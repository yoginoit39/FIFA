#!/bin/bash

# FIFA World Cup 2026 - Complete Startup Script
# Starts all services and monitors their health

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}FIFA World Cup 2026 - Complete Startup${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # Port is in use
    fi
    return 1  # Port is free
}

# Function to wait for service to be healthy
wait_for_service() {
    local name=$1
    local url=$2
    local max_attempts=60
    local attempt=1

    echo -e "${YELLOW}Waiting for $name to start...${NC}"

    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $name is UP!${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        ((attempt++))
    done

    echo -e "${RED}✗ $name failed to start${NC}"
    return 1
}

# Check if PostgreSQL is running
echo -e "${CYAN}[1/6] Checking PostgreSQL...${NC}"
if docker ps | grep -q worldcup2026-postgres; then
    echo -e "${GREEN}✓ PostgreSQL is already running${NC}"
else
    echo -e "${YELLOW}Starting PostgreSQL...${NC}"
    cd "$SCRIPT_DIR"
    docker-compose up -d postgres
    sleep 10
    echo -e "${GREEN}✓ PostgreSQL started${NC}"
fi
echo ""

# Build all backend services if needed
echo -e "${CYAN}[2/6] Building backend services...${NC}"
for service in match-service stadium-service ticket-service api-gateway; do
    if [ ! -f "$SCRIPT_DIR/backend/$service/target/${service}-1.0.0.jar" ]; then
        echo -e "${YELLOW}Building $service...${NC}"
        cd "$SCRIPT_DIR/backend/$service"
        ./mvnw clean package -DskipTests > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ $service built successfully${NC}"
        else
            echo -e "${RED}✗ $service build failed${NC}"
            exit 1
        fi
    else
        echo -e "${GREEN}✓ $service already built${NC}"
    fi
done
echo ""

# Start Match Service
echo -e "${CYAN}[3/6] Starting Match Service (Port 8081)...${NC}"
if check_port 8081; then
    echo -e "${GREEN}✓ Match Service already running${NC}"
else
    cd "$SCRIPT_DIR/backend/match-service"
    nohup ./mvnw spring-boot:run > "$SCRIPT_DIR/logs/match-service.log" 2>&1 &
    echo $! > "$SCRIPT_DIR/logs/match-service.pid"
    wait_for_service "Match Service" "http://localhost:8081/actuator/health"
fi
echo ""

# Start Stadium Service
echo -e "${CYAN}[4/6] Starting Stadium Service (Port 8082)...${NC}"
if check_port 8082; then
    echo -e "${GREEN}✓ Stadium Service already running${NC}"
else
    cd "$SCRIPT_DIR/backend/stadium-service"
    nohup ./mvnw spring-boot:run > "$SCRIPT_DIR/logs/stadium-service.log" 2>&1 &
    echo $! > "$SCRIPT_DIR/logs/stadium-service.pid"
    wait_for_service "Stadium Service" "http://localhost:8082/actuator/health"
fi
echo ""

# Start Ticket Service
echo -e "${CYAN}[5/6] Starting Ticket Service (Port 8083)...${NC}"
if check_port 8083; then
    echo -e "${GREEN}✓ Ticket Service already running${NC}"
else
    cd "$SCRIPT_DIR/backend/ticket-service"
    nohup ./mvnw spring-boot:run > "$SCRIPT_DIR/logs/ticket-service.log" 2>&1 &
    echo $! > "$SCRIPT_DIR/logs/ticket-service.pid"
    wait_for_service "Ticket Service" "http://localhost:8083/actuator/health"
fi
echo ""

# Start API Gateway
echo -e "${CYAN}[6/6] Starting API Gateway (Port 8080)...${NC}"
if check_port 8080; then
    echo -e "${GREEN}✓ API Gateway already running${NC}"
else
    cd "$SCRIPT_DIR/backend/api-gateway"
    nohup ./mvnw spring-boot:run > "$SCRIPT_DIR/logs/api-gateway.log" 2>&1 &
    echo $! > "$SCRIPT_DIR/logs/api-gateway.pid"
    sleep 15  # API Gateway takes longer to start
    wait_for_service "API Gateway" "http://localhost:8080/api/stadiums"
fi
echo ""

# Test all endpoints
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}Testing All Endpoints${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

test_endpoint() {
    local name=$1
    local url=$2

    echo -n "Testing $name... "
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>&1)

    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $response)"
        return 1
    fi
}

passed=0
failed=0

test_endpoint "GET /api/matches" "http://localhost:8080/api/matches"
[ $? -eq 0 ] && ((passed++)) || ((failed++))

test_endpoint "GET /api/stadiums" "http://localhost:8080/api/stadiums"
[ $? -eq 0 ] && ((passed++)) || ((failed++))

test_endpoint "GET /api/tickets" "http://localhost:8080/api/tickets"
[ $? -eq 0 ] && ((passed++)) || ((failed++))

test_endpoint "GET /api/matches/upcoming" "http://localhost:8080/api/matches/upcoming"
[ $? -eq 0 ] && ((passed++)) || ((failed++))

echo ""
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}Startup Complete!${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

if [ $failed -eq 0 ]; then
    echo -e "${GREEN}✓ All services are running successfully!${NC}"
    echo ""
    echo "Access points:"
    echo "  ${CYAN}Frontend:${NC}    http://localhost:5173 (start with: cd frontend && npm run dev)"
    echo "  ${CYAN}API Gateway:${NC} http://localhost:8080"
    echo "  ${CYAN}Match API:${NC}   http://localhost:8081"
    echo "  ${CYAN}Stadium API:${NC} http://localhost:8082"
    echo "  ${CYAN}Ticket API:${NC}  http://localhost:8083"
    echo ""
    echo "View logs:"
    echo "  ${CYAN}tail -f logs/match-service.log${NC}"
    echo "  ${CYAN}tail -f logs/stadium-service.log${NC}"
    echo "  ${CYAN}tail -f logs/ticket-service.log${NC}"
    echo "  ${CYAN}tail -f logs/api-gateway.log${NC}"
    echo ""
    echo -e "${YELLOW}Now start the frontend:${NC}"
    echo "  ${CYAN}cd frontend && npm run dev${NC}"
    echo ""
else
    echo -e "${RED}✗ Some services failed to start${NC}"
    echo ""
    echo "Check logs for errors:"
    echo "  tail -f logs/*.log"
    echo ""
    echo "To stop all services:"
    echo "  ./stop-all.sh"
fi

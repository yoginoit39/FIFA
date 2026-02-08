#!/bin/bash

# FIFA World Cup 2026 - Local Development Startup Script
# Run all services locally (PostgreSQL in Docker, rest local)

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}FIFA World Cup 2026 - Local Startup${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}Warning: Port $port is already in use${NC}"
        return 1
    fi
    return 0
}

# Step 1: Start PostgreSQL
echo -e "${GREEN}Step 1: Starting PostgreSQL...${NC}"
cd "$SCRIPT_DIR"
docker-compose up -d postgres
echo "Waiting for PostgreSQL to be ready..."
sleep 10

# Step 2: Start Match Service
echo -e "${GREEN}Step 2: Starting Match Service (Port 8081)...${NC}"
check_port 8081
cd "$SCRIPT_DIR/backend/match-service"
gnome-terminal --tab --title="Match Service" -- bash -c "./mvnw spring-boot:run; exec bash" 2>/dev/null ||
  osascript -e 'tell app "Terminal" to do script "cd '"$SCRIPT_DIR"'/backend/match-service && ./mvnw spring-boot:run"' 2>/dev/null ||
  echo "Please open a new terminal and run: cd $SCRIPT_DIR/backend/match-service && ./mvnw spring-boot:run"
sleep 5

# Step 3: Start Stadium Service
echo -e "${GREEN}Step 3: Starting Stadium Service (Port 8082)...${NC}"
check_port 8082
cd "$SCRIPT_DIR/backend/stadium-service"
gnome-terminal --tab --title="Stadium Service" -- bash -c "./mvnw spring-boot:run; exec bash" 2>/dev/null ||
  osascript -e 'tell app "Terminal" to do script "cd '"$SCRIPT_DIR"'/backend/stadium-service && ./mvnw spring-boot:run"' 2>/dev/null ||
  echo "Please open a new terminal and run: cd $SCRIPT_DIR/backend/stadium-service && ./mvnw spring-boot:run"
sleep 5

# Step 4: Start Ticket Service
echo -e "${GREEN}Step 4: Starting Ticket Service (Port 8083)...${NC}"
check_port 8083
cd "$SCRIPT_DIR/backend/ticket-service"
gnome-terminal --tab --title="Ticket Service" -- bash -c "./mvnw spring-boot:run; exec bash" 2>/dev/null ||
  osascript -e 'tell app "Terminal" to do script "cd '"$SCRIPT_DIR"'/backend/ticket-service && ./mvnw spring-boot:run"' 2>/dev/null ||
  echo "Please open a new terminal and run: cd $SCRIPT_DIR/backend/ticket-service && ./mvnw spring-boot:run"
sleep 5

# Step 5: Start API Gateway
echo -e "${GREEN}Step 5: Starting API Gateway (Port 8080)...${NC}"
check_port 8080
cd "$SCRIPT_DIR/backend/api-gateway"
gnome-terminal --tab --title="API Gateway" -- bash -c "./mvnw spring-boot:run; exec bash" 2>/dev/null ||
  osascript -e 'tell app "Terminal" to do script "cd '"$SCRIPT_DIR"'/backend/api-gateway && ./mvnw spring-boot:run"' 2>/dev/null ||
  echo "Please open a new terminal and run: cd $SCRIPT_DIR/backend/api-gateway && ./mvnw spring-boot:run"
sleep 5

# Step 6: Start Frontend
echo -e "${GREEN}Step 6: Starting Frontend (Port 5173)...${NC}"
check_port 5173
cd "$SCRIPT_DIR/frontend"
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi
gnome-terminal --tab --title="Frontend" -- bash -c "npm run dev; exec bash" 2>/dev/null ||
  osascript -e 'tell app "Terminal" to do script "cd '"$SCRIPT_DIR"'/frontend && npm run dev"' 2>/dev/null ||
  echo "Please open a new terminal and run: cd $SCRIPT_DIR/frontend && npm run dev"

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}All services are starting...${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Please wait 2-3 minutes for all services to fully start."
echo ""
echo "Access points:"
echo "  Frontend:    http://localhost:5173"
echo "  API Gateway: http://localhost:8080"
echo "  Match API:   http://localhost:8081"
echo "  Stadium API: http://localhost:8082"
echo "  Ticket API:  http://localhost:8083"
echo ""
echo -e "${YELLOW}Note: Each service will open in a new terminal window.${NC}"
echo -e "${YELLOW}Press Ctrl+C in each terminal to stop the services.${NC}"
echo ""

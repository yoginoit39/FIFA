#!/bin/bash

# FIFA World Cup 2026 - Stop All Services Script

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}Stopping All Services${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# Stop backend services using PID files
for service in match-service stadium-service ticket-service api-gateway; do
    pid_file="$SCRIPT_DIR/logs/${service}.pid"
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "Stopping ${service}... "
            kill $pid 2>/dev/null
            sleep 2
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                kill -9 $pid 2>/dev/null
            fi
            rm "$pid_file"
            echo -e "${GREEN}✓ ${service} stopped${NC}"
        else
            echo -e "${BLUE}${service} was not running${NC}"
            rm "$pid_file"
        fi
    else
        echo -e "${BLUE}${service} PID file not found${NC}"
    fi
done

# Kill any remaining Spring Boot processes on these ports
echo ""
echo "Checking for any remaining processes on ports..."

for port in 8080 8081 8082 8083; do
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        echo -e "Killing process on port $port (PID: $pid)..."
        kill -9 $pid 2>/dev/null
        echo -e "${GREEN}✓ Process on port $port stopped${NC}"
    fi
done

# Stop PostgreSQL
echo ""
echo "Stopping PostgreSQL..."
cd "$SCRIPT_DIR"
docker-compose down
echo -e "${GREEN}✓ PostgreSQL stopped${NC}"

echo ""
echo -e "${GREEN}All services stopped!${NC}"

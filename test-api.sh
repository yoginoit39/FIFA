#!/bin/bash

# FIFA World Cup 2026 - API Testing Script
# Tests all backend services and verifies connectivity

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base URLs
API_GATEWAY_URL="http://localhost:8080"
MATCH_SERVICE_URL="http://localhost:8081"
STADIUM_SERVICE_URL="http://localhost:8082"
TICKET_SERVICE_URL="http://localhost:8083"
FRONTEND_URL="http://localhost:5173"

echo "========================================"
echo "FIFA World Cup 2026 - API Testing"
echo "========================================"
echo ""

# Function to test endpoint
test_endpoint() {
    local service_name=$1
    local url=$2
    local description=$3

    echo -n "Testing ${BLUE}${service_name}${NC} - ${description}... "

    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>&1)

    if [ "$response" = "200" ] || [ "$response" = "201" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $response)"
        return 1
    fi
}

# Function to test with JSON output
test_endpoint_with_data() {
    local service_name=$1
    local url=$2
    local description=$3

    echo -n "Testing ${BLUE}${service_name}${NC} - ${description}... "

    response=$(curl -s "$url" 2>&1)
    http_code=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>&1)

    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        echo "  Response: ${response:0:100}..."
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $http_code)"
        return 1
    fi
}

passed=0
failed=0

echo "========================================"
echo "1. Testing Frontend"
echo "========================================"
test_endpoint "Frontend" "$FRONTEND_URL" "React App"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi
echo ""

echo "========================================"
echo "2. Testing API Gateway"
echo "========================================"
test_endpoint "API Gateway" "$API_GATEWAY_URL/actuator/health" "Health Check"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi
echo ""

echo "========================================"
echo "3. Testing Match Service (Direct)"
echo "========================================"
test_endpoint "Match Service" "$MATCH_SERVICE_URL/actuator/health" "Health Check"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "Match Service" "$MATCH_SERVICE_URL/api/matches" "Get All Matches"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "Match Service" "$MATCH_SERVICE_URL/api/matches/upcoming" "Get Upcoming Matches"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "Match Service" "$MATCH_SERVICE_URL/api/teams" "Get All Teams"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi
echo ""

echo "========================================"
echo "4. Testing Stadium Service (Direct)"
echo "========================================"
test_endpoint "Stadium Service" "$STADIUM_SERVICE_URL/actuator/health" "Health Check"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "Stadium Service" "$STADIUM_SERVICE_URL/api/stadiums" "Get All Stadiums"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "Stadium Service" "$STADIUM_SERVICE_URL/api/stadiums/1" "Get Stadium by ID"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi
echo ""

echo "========================================"
echo "5. Testing Ticket Service (Direct)"
echo "========================================"
test_endpoint "Ticket Service" "$TICKET_SERVICE_URL/actuator/health" "Health Check"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "Ticket Service" "$TICKET_SERVICE_URL/api/tickets" "Get All Ticket Links"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi
echo ""

echo "========================================"
echo "6. Testing API Gateway Routes"
echo "========================================"
test_endpoint_with_data "API Gateway" "$API_GATEWAY_URL/api/matches" "Route to Match Service"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "API Gateway" "$API_GATEWAY_URL/api/stadiums" "Route to Stadium Service"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi

test_endpoint_with_data "API Gateway" "$API_GATEWAY_URL/api/tickets" "Route to Ticket Service"
if [ $? -eq 0 ]; then ((passed++)); else ((failed++)); fi
echo ""

echo "========================================"
echo "7. Testing Database Initialization"
echo "========================================"
echo "Checking if stadiums were seeded..."
stadium_count=$(curl -s "$STADIUM_SERVICE_URL/api/stadiums" | grep -o '"id"' | wc -l)
if [ "$stadium_count" -ge 16 ]; then
    echo -e "${GREEN}✓ PASS${NC} - Found $stadium_count stadiums (expected 16)"
    ((passed++))
else
    echo -e "${RED}✗ FAIL${NC} - Found $stadium_count stadiums (expected 16)"
    ((failed++))
fi
echo ""

echo "========================================"
echo "Test Summary"
echo "========================================"
total=$((passed + failed))
percentage=$((passed * 100 / total))

echo -e "Total Tests: ${BLUE}${total}${NC}"
echo -e "Passed: ${GREEN}${passed}${NC}"
echo -e "Failed: ${RED}${failed}${NC}"
echo -e "Success Rate: ${BLUE}${percentage}%${NC}"
echo ""

if [ $failed -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
    echo "🚀 Application is fully functional!"
    echo ""
    echo "Access the application at:"
    echo "  Frontend: $FRONTEND_URL"
    echo "  API Gateway: $API_GATEWAY_URL"
    exit 0
else
    echo -e "${YELLOW}⚠ Some tests failed.${NC}"
    echo ""
    echo "Troubleshooting steps:"
    echo "  1. Check if all containers are running: docker-compose ps"
    echo "  2. Check logs: docker-compose logs"
    echo "  3. Restart services: docker-compose restart"
    echo "  4. See QUICKSTART.md for detailed troubleshooting"
    exit 1
fi

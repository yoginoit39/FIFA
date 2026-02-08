#!/bin/bash

# FIFA World Cup 2026 - Automated Diagnostics Script
# Detects and reports all issues preventing services from starting

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo -e "${MAGENTA}========================================${NC}"
echo -e "${MAGENTA}FIFA World Cup 2026 - DIAGNOSTICS${NC}"
echo -e "${MAGENTA}========================================${NC}"
echo ""

ERRORS_FOUND=0

# Function to report error
report_error() {
    echo -e "${RED}✗ ERROR: $1${NC}"
    ((ERRORS_FOUND++))
}

# Function to report warning
report_warning() {
    echo -e "${YELLOW}⚠ WARNING: $1${NC}"
}

# Function to report success
report_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# 1. Check Java version
echo -e "${CYAN}[1/10] Checking Java...${NC}"
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -n 1)
    echo "  Found: $java_version"

    version_num=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$version_num" -ge 17 ]; then
        report_success "Java 17+ detected"
    else
        report_error "Java 17 or higher required, found version $version_num"
    fi
else
    report_error "Java not found. Install Java 17 from https://adoptium.net/"
fi
echo ""

# 2. Check Maven
echo -e "${CYAN}[2/10] Checking Maven...${NC}"
if command -v mvn &> /dev/null; then
    mvn_version=$(mvn -version 2>&1 | head -n 1)
    echo "  Found: $mvn_version"
    report_success "Maven detected"
else
    report_error "Maven not found. Install from https://maven.apache.org/"
fi
echo ""

# 3. Check Node.js
echo -e "${CYAN}[3/10] Checking Node.js...${NC}"
if command -v node &> /dev/null; then
    node_version=$(node --version)
    echo "  Found: $node_version"
    report_success "Node.js detected"
else
    report_warning "Node.js not found (only needed for frontend)"
fi
echo ""

# 4. Check Docker
echo -e "${CYAN}[4/10] Checking Docker...${NC}"
if command -v docker &> /dev/null; then
    docker_version=$(docker --version)
    echo "  Found: $docker_version"
    report_success "Docker detected"

    if docker ps &> /dev/null; then
        report_success "Docker daemon is running"
    else
        report_error "Docker daemon is not running. Start Docker Desktop."
    fi
else
    report_error "Docker not found. Install Docker Desktop from https://docker.com/products/docker-desktop"
fi
echo ""

# 5. Check PostgreSQL
echo -e "${CYAN}[5/10] Checking PostgreSQL...${NC}"
if docker ps | grep -q worldcup2026-postgres; then
    report_success "PostgreSQL container is running"

    # Test connection
    if docker exec worldcup2026-postgres pg_isready -U worldcup_user &> /dev/null; then
        report_success "PostgreSQL is accepting connections"

        # Check if tables exist
        stadium_count=$(docker exec worldcup2026-postgres psql -U worldcup_user -d worldcup2026_db -tAc "SELECT COUNT(*) FROM stadium_service_schema.stadiums;" 2>/dev/null)
        if [ ! -z "$stadium_count" ]; then
            echo "  Database initialized: $stadium_count stadiums found"
            report_success "Database schema initialized"
        else
            report_warning "Database schema may not be initialized"
        fi
    else
        report_error "PostgreSQL not accepting connections"
    fi
else
    report_error "PostgreSQL container not running. Run: docker-compose up -d postgres"
fi
echo ""

# 6. Check port conflicts
echo -e "${CYAN}[6/10] Checking for port conflicts...${NC}"
for port in 5432 8080 8081 8082 8083 5173; do
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        pid=$(lsof -ti:$port)
        process=$(ps -p $pid -o comm= 2>/dev/null)
        echo "  Port $port: Used by PID $pid ($process)"

        if [ $port -ne 5432 ]; then  # PostgreSQL is expected on 5432
            report_warning "Port $port is in use (may be okay if it's our service)"
        fi
    else
        echo "  Port $port: Available"
    fi
done
echo ""

# 7. Check if JARs are built
echo -e "${CYAN}[7/10] Checking backend builds...${NC}"
for service in match-service stadium-service ticket-service api-gateway; do
    jar_file="$SCRIPT_DIR/backend/$service/target/${service}-1.0.0.jar"
    if [ -f "$jar_file" ]; then
        size=$(ls -lh "$jar_file" | awk '{print $5}')
        echo "  $service: Built ($size)"
        report_success "$service JAR exists"
    else
        report_error "$service not built. Run: cd backend/$service && ./mvnw clean package"
    fi
done
echo ""

# 8. Check log files for errors
echo -e "${CYAN}[8/10] Analyzing log files for errors...${NC}"
if [ -d "$SCRIPT_DIR/logs" ]; then
    for log_file in "$SCRIPT_DIR/logs"/*.log; do
        if [ -f "$log_file" ]; then
            service_name=$(basename "$log_file" .log)
            echo ""
            echo -e "${BLUE}=== $service_name ===${NC}"

            # Get last 50 lines and look for errors
            tail -n 50 "$log_file" > /tmp/log_snippet.txt

            # Check for common error patterns
            if grep -i "error\|exception\|failed\|refused" /tmp/log_snippet.txt | head -5; then
                report_error "Errors found in $service_name.log (see above)"
                echo ""
                echo -e "${YELLOW}Last 20 lines of $service_name.log:${NC}"
                tail -n 20 "$log_file"
                echo ""
            else
                # Show last few lines anyway
                echo "Last 10 lines:"
                tail -n 10 "$log_file"
            fi
        fi
    done
    rm -f /tmp/log_snippet.txt
else
    echo "  No logs directory found yet"
fi
echo ""

# 9. Check application.yml files
echo -e "${CYAN}[9/10] Checking configuration files...${NC}"
for service in match-service stadium-service ticket-service api-gateway; do
    config_file="$SCRIPT_DIR/backend/$service/src/main/resources/application.yml"
    if [ -f "$config_file" ]; then
        echo "  $service: application.yml exists"

        # Check for placeholder values that need to be replaced
        if grep -q "your_api_key_here\|change_in_production" "$config_file"; then
            report_warning "$service has placeholder values in config (may need API keys)"
        fi
    else
        report_error "$service: application.yml not found"
    fi
done
echo ""

# 10. Try to start each service individually and capture errors
echo -e "${CYAN}[10/10] Testing service startup (quick test)...${NC}"
echo "This will attempt to start each service briefly to detect startup errors..."
echo ""

test_service_startup() {
    local service=$1
    local port=$2

    echo -e "${BLUE}Testing $service...${NC}"
    cd "$SCRIPT_DIR/backend/$service"

    # Try to start with timeout
    timeout 30s ./mvnw spring-boot:run > /tmp/${service}_test.log 2>&1 &
    local pid=$!

    sleep 15

    if ps -p $pid > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $service started successfully${NC}"
        kill $pid 2>/dev/null
        wait $pid 2>/dev/null
    else
        echo -e "${RED}✗ $service failed to start${NC}"
        echo "Error output:"
        tail -n 30 /tmp/${service}_test.log
        report_error "$service startup failed (see output above)"
    fi

    rm -f /tmp/${service}_test.log
    echo ""
}

# Only test if user confirms (can take time)
echo -e "${YELLOW}Do you want to test actual service startup? (takes ~1 min per service) [y/N]${NC}"
read -t 5 -n 1 response
echo ""

if [[ "$response" =~ ^[Yy]$ ]]; then
    for service in match-service stadium-service ticket-service api-gateway; do
        test_service_startup "$service"
    done
else
    echo "Skipping startup tests. You can run manually with:"
    echo "  cd backend/match-service && ./mvnw spring-boot:run"
fi

# Summary
echo ""
echo -e "${MAGENTA}========================================${NC}"
echo -e "${MAGENTA}DIAGNOSTIC SUMMARY${NC}"
echo -e "${MAGENTA}========================================${NC}"
echo ""

if [ $ERRORS_FOUND -eq 0 ]; then
    echo -e "${GREEN}✓ No critical errors detected!${NC}"
    echo ""
    echo "If services are still failing, check:"
    echo "  1. Run: ./start-all.sh and watch for errors"
    echo "  2. Check logs: tail -f logs/*.log"
    echo "  3. Try starting services manually one by one"
else
    echo -e "${RED}✗ Found $ERRORS_FOUND error(s)${NC}"
    echo ""
    echo "Please fix the errors above and try again."
    echo ""
    echo "Common fixes:"
    echo "  1. Install missing prerequisites (Java 17, Maven, Docker)"
    echo "  2. Start Docker Desktop"
    echo "  3. Start PostgreSQL: docker-compose up -d postgres"
    echo "  4. Build services: cd backend/match-service && ./mvnw clean package"
    echo "  5. Check logs for specific errors: tail -f logs/*.log"
fi

echo ""
echo -e "${CYAN}Generated full diagnostic report.${NC}"
echo -e "${CYAN}Share this output to get help debugging.${NC}"
echo ""

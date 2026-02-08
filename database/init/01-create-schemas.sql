-- FIFA World Cup 2026 Database Initialization
-- PostgreSQL 16
-- Schema-per-Service Architecture

-- ============================================================================
-- DROP EXISTING SCHEMAS (for clean re-initialization)
-- ============================================================================
DROP SCHEMA IF EXISTS match_service_schema CASCADE;
DROP SCHEMA IF EXISTS stadium_service_schema CASCADE;
DROP SCHEMA IF EXISTS ticket_service_schema CASCADE;
DROP SCHEMA IF EXISTS gateway_service_schema CASCADE;

-- ============================================================================
-- CREATE SCHEMAS
-- ============================================================================
CREATE SCHEMA match_service_schema;
CREATE SCHEMA stadium_service_schema;
CREATE SCHEMA ticket_service_schema;
CREATE SCHEMA gateway_service_schema;

-- ============================================================================
-- MATCH SERVICE SCHEMA
-- ============================================================================

-- Teams Table
CREATE TABLE match_service_schema.teams (
    id BIGSERIAL PRIMARY KEY,
    external_api_id VARCHAR(50) UNIQUE,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500),
    fifa_ranking INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Matches Table
CREATE TABLE match_service_schema.matches (
    id BIGSERIAL PRIMARY KEY,
    external_api_id VARCHAR(50) UNIQUE,
    home_team_id BIGINT REFERENCES match_service_schema.teams(id),
    away_team_id BIGINT REFERENCES match_service_schema.teams(id),
    stadium_id BIGINT,  -- Loose coupling to stadium service
    match_date DATE NOT NULL,
    match_time TIME,
    status VARCHAR(50) DEFAULT 'SCHEDULED',  -- SCHEDULED, LIVE, FINISHED, POSTPONED, CANCELLED
    home_score INTEGER DEFAULT 0,
    away_score INTEGER DEFAULT 0,
    round VARCHAR(50),  -- Group Stage, Round of 16, Quarter-final, Semi-final, Final
    group_name VARCHAR(10),  -- Group A, B, C, etc.
    venue_name VARCHAR(200),
    venue_city VARCHAR(100),
    venue_country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_different_teams CHECK (home_team_id != away_team_id)
);

-- Indexes for performance
CREATE INDEX idx_matches_date ON match_service_schema.matches(match_date);
CREATE INDEX idx_matches_status ON match_service_schema.matches(status);
CREATE INDEX idx_matches_stadium ON match_service_schema.matches(stadium_id);
CREATE INDEX idx_matches_home_team ON match_service_schema.matches(home_team_id);
CREATE INDEX idx_matches_away_team ON match_service_schema.matches(away_team_id);
CREATE INDEX idx_matches_round ON match_service_schema.matches(round);

-- ============================================================================
-- STADIUM SERVICE SCHEMA
-- ============================================================================

-- Stadiums Table
CREATE TABLE stadium_service_schema.stadiums (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    capacity INTEGER,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    address TEXT,
    image_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_stadiums_city ON stadium_service_schema.stadiums(city);
CREATE INDEX idx_stadiums_country ON stadium_service_schema.stadiums(country);

-- ============================================================================
-- TICKET SERVICE SCHEMA
-- ============================================================================

-- Ticket Links Table
CREATE TABLE ticket_service_schema.ticket_links (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,  -- Loose coupling to match service
    provider_name VARCHAR(100) NOT NULL,  -- FIFA, Ticketmaster, StubHub, SeatGeek
    booking_url TEXT NOT NULL,
    price_range VARCHAR(100),  -- e.g., "$50 - $500"
    availability_status VARCHAR(50) DEFAULT 'AVAILABLE',  -- AVAILABLE, SOLD_OUT, NOT_YET_AVAILABLE
    priority INTEGER DEFAULT 1,  -- Display order (1 = highest)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_ticket_links_match ON ticket_service_schema.ticket_links(match_id);
CREATE INDEX idx_ticket_links_provider ON ticket_service_schema.ticket_links(provider_name);

-- ============================================================================
-- GATEWAY SERVICE SCHEMA (Authentication - Optional)
-- ============================================================================

-- Users Table (for JWT authentication)
CREATE TABLE gateway_service_schema.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- BCrypt hash
    role VARCHAR(20) DEFAULT 'USER',  -- USER, ADMIN
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_users_username ON gateway_service_schema.users(username);
CREATE INDEX idx_users_email ON gateway_service_schema.users(email);

-- ============================================================================
-- SEED DATA - FIFA World Cup 2026 Stadiums
-- ============================================================================

INSERT INTO stadium_service_schema.stadiums
(name, city, state, country, capacity, latitude, longitude, address, description) VALUES
('MetLife Stadium', 'East Rutherford', 'New Jersey', 'USA', 82500, 40.81361100, -74.07444400,
 '1 MetLife Stadium Dr, East Rutherford, NJ 07073', 'Home of the New York Giants and Jets, hosting the World Cup Final'),

('AT&T Stadium', 'Arlington', 'Texas', 'USA', 80000, 32.74722200, -97.09277800,
 '1 AT&T Way, Arlington, TX 76011', 'State-of-the-art stadium with retractable roof'),

('Arrowhead Stadium', 'Kansas City', 'Missouri', 'USA', 76416, 39.04888900, -94.48388900,
 '1 Arrowhead Dr, Kansas City, MO 64129', 'One of the loudest stadiums in the NFL'),

('Mercedes-Benz Stadium', 'Atlanta', 'Georgia', 'USA', 71000, 33.75555600, -84.40083300,
 '1 AMB Dr NW, Atlanta, GA 30313', 'Modern stadium with unique retractable roof design'),

('NRG Stadium', 'Houston', 'Texas', 'USA', 72220, 29.68472200, -95.41083300,
 'NRG Pkwy, Houston, TX 77054', 'First NFL stadium with a retractable roof'),

('Lincoln Financial Field', 'Philadelphia', 'Pennsylvania', 'USA', 69176, 39.90083300, -75.16750000,
 '1 Lincoln Financial Field Way, Philadelphia, PA 19148', 'Home of the Philadelphia Eagles'),

('Lumen Field', 'Seattle', 'Washington', 'USA', 69000, 47.59527800, -122.33166700,
 '800 Occidental Ave S, Seattle, WA 98134', 'Known for its passionate soccer fans'),

('SoFi Stadium', 'Inglewood', 'California', 'USA', 70240, 33.95361100, -118.33833300,
 '1001 Stadium Dr, Inglewood, CA 90301', 'State-of-the-art indoor-outdoor stadium'),

('Levi''s Stadium', 'Santa Clara', 'California', 'USA', 68500, 37.40305600, -121.96972200,
 '4900 Marie P DeBartolo Way, Santa Clara, CA 95054', 'Home of the San Francisco 49ers'),

('Gillette Stadium', 'Foxborough', 'Massachusetts', 'USA', 65878, 42.09083300, -71.26444400,
 '1 Patriot Pl, Foxborough, MA 02035', 'Home of the New England Patriots'),

('Hard Rock Stadium', 'Miami Gardens', 'Florida', 'USA', 64767, 25.95805600, -80.23888900,
 '347 Don Shula Dr, Miami Gardens, FL 33056', 'Tropical venue with partial roof coverage'),

('Estadio Azteca', 'Mexico City', NULL, 'Mexico', 87523, 19.30305600, -99.15055600,
 'Calz. de Tlalpan 3465, Mexico City', 'Legendary stadium, third time hosting World Cup matches'),

('Estadio BBVA', 'Monterrey', 'Nuevo León', 'Mexico', 53500, 25.72083300, -100.24500000,
 'Av. Pablo Livas 2011, Monterrey', 'Modern stadium with stunning mountain views'),

('Estadio Akron', 'Guadalajara', 'Jalisco', 'Mexico', 46232, 20.69055600, -103.46416700,
 'Av. Akron s/n, Zapopan', 'Home of Club Deportivo Guadalajara (Chivas)'),

('BMO Field', 'Toronto', 'Ontario', 'Canada', 45500, 43.63305600, -79.41861100,
 '170 Princes'' Blvd, Toronto, ON M6K 3C3', 'Canada''s premier soccer-specific stadium'),

('BC Place', 'Vancouver', 'British Columbia', 'Canada', 54500, 49.27666700, -123.11194400,
 '777 Pacific Blvd, Vancouver, BC V6B 4Y8', 'Retractable roof stadium in downtown Vancouver');

-- ============================================================================
-- SEED DATA - Sample Teams (will be populated by API sync)
-- ============================================================================

INSERT INTO match_service_schema.teams
(external_api_id, name, country, fifa_ranking) VALUES
('USA', 'United States', 'USA', 13),
('MEX', 'Mexico', 'Mexico', 12),
('CAN', 'Canada', 'Canada', 41),
('BRA', 'Brazil', 'Brazil', 1),
('ARG', 'Argentina', 'Argentina', 2),
('FRA', 'France', 'France', 3),
('ENG', 'England', 'England', 4),
('ESP', 'Spain', 'Spain', 8),
('GER', 'Germany', 'Germany', 11),
('POR', 'Portugal', 'Portugal', 6);

-- ============================================================================
-- SEED DATA - Sample Ticket Providers
-- ============================================================================

INSERT INTO ticket_service_schema.ticket_links
(match_id, provider_name, booking_url, price_range, availability_status, priority) VALUES
(1, 'FIFA Official', 'https://www.fifa.com/tickets', '$100 - $1000', 'NOT_YET_AVAILABLE', 1),
(1, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$150 - $1500', 'NOT_YET_AVAILABLE', 2),
(1, 'StubHub', 'https://www.stubhub.com/world-cup', '$200 - $2000', 'NOT_YET_AVAILABLE', 3),
(1, 'SeatGeek', 'https://seatgeek.com/world-cup', '$175 - $1800', 'NOT_YET_AVAILABLE', 4);

-- ============================================================================
-- SEED DATA - Sample Admin User
-- ============================================================================

-- Password: 'admin123' (BCrypt hash - CHANGE IN PRODUCTION!)
INSERT INTO gateway_service_schema.users
(username, email, password_hash, role) VALUES
('admin', 'admin@worldcup2026.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4S.K9proL8oRz2Fy4JxkKC7.yCMoO', 'ADMIN');

-- ============================================================================
-- GRANT PERMISSIONS
-- ============================================================================

-- Grant access to application user (will be created when connecting)
-- This assumes the application connects with a user named 'worldcup_user'

GRANT USAGE ON SCHEMA match_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA match_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA match_service_schema TO worldcup_user;

GRANT USAGE ON SCHEMA stadium_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA stadium_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA stadium_service_schema TO worldcup_user;

GRANT USAGE ON SCHEMA ticket_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ticket_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ticket_service_schema TO worldcup_user;

GRANT USAGE ON SCHEMA gateway_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA gateway_service_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA gateway_service_schema TO worldcup_user;

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Uncomment to verify schema creation:
-- SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE '%service%';
-- SELECT table_schema, table_name FROM information_schema.tables WHERE table_schema LIKE '%service%';
-- SELECT COUNT(*) FROM stadium_service_schema.stadiums;
-- SELECT COUNT(*) FROM match_service_schema.teams;

-- ============================================================================
-- END OF INITIALIZATION SCRIPT
-- ============================================================================

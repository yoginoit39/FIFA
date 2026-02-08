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
    min_price INTEGER,  -- Minimum ticket price for sorting
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
-- SEED DATA - Sample Matches
-- ============================================================================

INSERT INTO match_service_schema.matches
(home_team_id, away_team_id, match_date, match_time, status, home_score, away_score, round, group_name, venue_name, venue_city, venue_country) VALUES
(1, 2, '2026-06-11', '20:00:00', 'SCHEDULED', 0, 0, 'Opening Match', 'N/A', 'MetLife Stadium', 'East Rutherford', 'USA'),
(6, 9, '2026-06-12', '14:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group A', 'AT&T Stadium', 'Arlington', 'USA'),
(4, 10, '2026-06-12', '17:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group A', 'Hard Rock Stadium', 'Miami Gardens', 'USA'),
(6, 10, '2026-06-16', '14:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group A', 'Mercedes-Benz Stadium', 'Atlanta', 'USA'),
(4, 9, '2026-06-16', '20:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group A', 'SoFi Stadium', 'Inglewood', 'USA'),
(7, 5, '2026-06-13', '11:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group B', 'Lumen Field', 'Seattle', 'USA'),
(8, 3, '2026-06-13', '14:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group B', 'BMO Field', 'Toronto', 'Canada'),
(7, 3, '2026-06-17', '17:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group B', 'BC Place', 'Vancouver', 'Canada'),
(8, 5, '2026-06-17', '20:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group B', 'Estadio Azteca', 'Mexico City', 'Mexico'),
(1, 4, '2026-06-14', '20:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group C', 'NRG Stadium', 'Houston', 'USA'),
(2, 6, '2026-06-14', '17:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group C', 'Estadio BBVA', 'Monterrey', 'Mexico'),
(9, 7, '2026-06-15', '14:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group D', 'Arrowhead Stadium', 'Kansas City', 'USA'),
(10, 8, '2026-06-15', '11:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group D', 'Estadio Akron', 'Guadalajara', 'Mexico'),
(4, 7, '2026-06-30', '16:00:00', 'SCHEDULED', 0, 0, 'Round of 16', 'N/A', 'Levi''s Stadium', 'Santa Clara', 'USA'),
(6, 5, '2026-06-30', '19:00:00', 'SCHEDULED', 0, 0, 'Round of 16', 'N/A', 'Gillette Stadium', 'Foxborough', 'USA'),
(1, 9, '2026-07-01', '16:00:00', 'SCHEDULED', 0, 0, 'Round of 16', 'N/A', 'Lincoln Financial Field', 'Philadelphia', 'USA'),
(4, 5, '2026-06-22', '14:30:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group E', 'Estadio Azteca', 'Mexico City', 'Mexico'),
(7, 8, '2026-06-22', '18:00:00', 'SCHEDULED', 0, 0, 'Group Stage', 'Group E', 'Lumen Field', 'Seattle', 'USA');

-- ============================================================================
-- SEED DATA - Ticket Providers (all 18 matches)
-- ============================================================================

INSERT INTO ticket_service_schema.ticket_links
(match_id, provider_name, booking_url, price_range, availability_status, priority, min_price) VALUES
(1, 'SeatGeek', 'https://seatgeek.com/world-cup', '$310 - $2200', 'AVAILABLE', 1, 310),
(1, 'FIFA Official', 'https://www.fifa.com/tickets', '$350 - $2500', 'AVAILABLE', 2, 350),
(1, 'StubHub', 'https://www.stubhub.com/world-cup', '$390 - $2800', 'AVAILABLE', 3, 390),
(1, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$420 - $3000', 'AVAILABLE', 4, 420),
(2, 'StubHub', 'https://www.stubhub.com/world-cup', '$98 - $720', 'AVAILABLE', 1, 98),
(2, 'SeatGeek', 'https://seatgeek.com/world-cup', '$110 - $780', 'AVAILABLE', 2, 110),
(2, 'FIFA Official', 'https://www.fifa.com/tickets', '$120 - $800', 'AVAILABLE', 3, 120),
(2, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$145 - $950', 'AVAILABLE', 4, 145),
(3, 'StubHub', 'https://www.stubhub.com/world-cup', '$105 - $820', 'AVAILABLE', 1, 105),
(3, 'SeatGeek', 'https://seatgeek.com/world-cup', '$119 - $860', 'AVAILABLE', 2, 119),
(3, 'FIFA Official', 'https://www.fifa.com/tickets', '$130 - $900', 'AVAILABLE', 3, 130),
(3, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$155 - $1050', 'AVAILABLE', 4, 155),
(4, 'StubHub', 'https://www.stubhub.com/world-cup', '$88 - $700', 'AVAILABLE', 1, 88),
(4, 'SeatGeek', 'https://seatgeek.com/world-cup', '$95 - $720', 'AVAILABLE', 2, 95),
(4, 'FIFA Official', 'https://www.fifa.com/tickets', '$110 - $750', 'AVAILABLE', 3, 110),
(4, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$130 - $880', 'AVAILABLE', 4, 130),
(5, 'StubHub', 'https://www.stubhub.com/world-cup', '$99 - $790', 'AVAILABLE', 1, 99),
(5, 'SeatGeek', 'https://seatgeek.com/world-cup', '$112 - $810', 'AVAILABLE', 2, 112),
(5, 'FIFA Official', 'https://www.fifa.com/tickets', '$125 - $850', 'AVAILABLE', 3, 125),
(5, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$148 - $990', 'AVAILABLE', 4, 148),
(6, 'StubHub', 'https://www.stubhub.com/world-cup', '$118 - $980', 'AVAILABLE', 1, 118),
(6, 'SeatGeek', 'https://seatgeek.com/world-cup', '$128 - $1050', 'AVAILABLE', 2, 128),
(6, 'FIFA Official', 'https://www.fifa.com/tickets', '$140 - $1100', 'AVAILABLE', 3, 140),
(6, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$165 - $1300', 'AVAILABLE', 4, 165),
(7, 'StubHub', 'https://www.stubhub.com/world-cup', '$65 - $460', 'AVAILABLE', 1, 65),
(7, 'SeatGeek', 'https://seatgeek.com/world-cup', '$72 - $490', 'AVAILABLE', 2, 72),
(7, 'FIFA Official', 'https://www.fifa.com/tickets', '$80 - $500', 'AVAILABLE', 3, 80),
(7, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$95 - $580', 'AVAILABLE', 4, 95),
(8, 'StubHub', 'https://www.stubhub.com/world-cup', '$60 - $440', 'AVAILABLE', 1, 60),
(8, 'SeatGeek', 'https://seatgeek.com/world-cup', '$68 - $470', 'AVAILABLE', 2, 68),
(8, 'FIFA Official', 'https://www.fifa.com/tickets', '$75 - $480', 'AVAILABLE', 3, 75),
(8, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$88 - $550', 'AVAILABLE', 4, 88),
(9, 'StubHub', 'https://www.stubhub.com/world-cup', '$108 - $880', 'AVAILABLE', 1, 108),
(9, 'SeatGeek', 'https://seatgeek.com/world-cup', '$122 - $900', 'AVAILABLE', 2, 122),
(9, 'FIFA Official', 'https://www.fifa.com/tickets', '$135 - $950', 'AVAILABLE', 3, 135),
(9, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$158 - $1100', 'AVAILABLE', 4, 158),
(10, 'StubHub', 'https://www.stubhub.com/world-cup', '$132 - $1100', 'AVAILABLE', 1, 132),
(10, 'SeatGeek', 'https://seatgeek.com/world-cup', '$145 - $1150', 'AVAILABLE', 2, 145),
(10, 'FIFA Official', 'https://www.fifa.com/tickets', '$160 - $1200', 'AVAILABLE', 3, 160),
(10, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$188 - $1400', 'AVAILABLE', 4, 188),
(11, 'StubHub', 'https://www.stubhub.com/world-cup', '$72 - $550', 'AVAILABLE', 1, 72),
(11, 'SeatGeek', 'https://seatgeek.com/world-cup', '$82 - $580', 'AVAILABLE', 2, 82),
(11, 'FIFA Official', 'https://www.fifa.com/tickets', '$90 - $600', 'AVAILABLE', 3, 90),
(11, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$108 - $700', 'AVAILABLE', 4, 108),
(12, 'StubHub', 'https://www.stubhub.com/world-cup', '$92 - $720', 'AVAILABLE', 1, 92),
(12, 'SeatGeek', 'https://seatgeek.com/world-cup', '$104 - $750', 'AVAILABLE', 2, 104),
(12, 'FIFA Official', 'https://www.fifa.com/tickets', '$115 - $780', 'AVAILABLE', 3, 115),
(12, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$138 - $920', 'AVAILABLE', 4, 138),
(13, 'StubHub', 'https://www.stubhub.com/world-cup', '$96 - $760', 'AVAILABLE', 1, 96),
(13, 'SeatGeek', 'https://seatgeek.com/world-cup', '$108 - $790', 'AVAILABLE', 2, 108),
(13, 'FIFA Official', 'https://www.fifa.com/tickets', '$120 - $820', 'AVAILABLE', 3, 120),
(13, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$142 - $960', 'AVAILABLE', 4, 142),
(14, 'StubHub', 'https://www.stubhub.com/world-cup', '$175 - $1400', 'AVAILABLE', 1, 175),
(14, 'SeatGeek', 'https://seatgeek.com/world-cup', '$188 - $1450', 'AVAILABLE', 2, 188),
(14, 'FIFA Official', 'https://www.fifa.com/tickets', '$200 - $1500', 'AVAILABLE', 3, 200),
(14, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$238 - $1800', 'AVAILABLE', 4, 238),
(15, 'StubHub', 'https://www.stubhub.com/world-cup', '$218 - $1850', 'AVAILABLE', 1, 218),
(15, 'SeatGeek', 'https://seatgeek.com/world-cup', '$235 - $1950', 'AVAILABLE', 2, 235),
(15, 'FIFA Official', 'https://www.fifa.com/tickets', '$250 - $2000', 'AVAILABLE', 3, 250),
(15, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$295 - $2400', 'AVAILABLE', 4, 295),
(16, 'StubHub', 'https://www.stubhub.com/world-cup', '$192 - $1650', 'AVAILABLE', 1, 192),
(16, 'SeatGeek', 'https://seatgeek.com/world-cup', '$208 - $1720', 'AVAILABLE', 2, 208),
(16, 'FIFA Official', 'https://www.fifa.com/tickets', '$220 - $1800', 'AVAILABLE', 3, 220),
(16, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$260 - $2100', 'AVAILABLE', 4, 260),
(17, 'StubHub', 'https://www.stubhub.com/world-cup', '$38 - $185', 'AVAILABLE', 1, 38),
(17, 'SeatGeek', 'https://seatgeek.com/world-cup', '$42 - $195', 'AVAILABLE', 2, 42),
(17, 'FIFA Official', 'https://www.fifa.com/tickets', '$45 - $200', 'AVAILABLE', 3, 45),
(17, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', '$52 - $240', 'AVAILABLE', 4, 52),
(18, 'FIFA Official', 'https://www.fifa.com/tickets', 'Sold Out', 'SOLD_OUT', 1, 0),
(18, 'Ticketmaster', 'https://www.ticketmaster.com/world-cup', 'Sold Out', 'SOLD_OUT', 2, 0),
(18, 'StubHub', 'https://www.stubhub.com/world-cup', 'Sold Out', 'SOLD_OUT', 3, 0),
(18, 'SeatGeek', 'https://seatgeek.com/world-cup', 'Sold Out', 'SOLD_OUT', 4, 0);

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

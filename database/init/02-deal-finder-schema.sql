-- ============================================================================
-- DEAL FINDER SERVICE SCHEMA
-- FIFA World Cup 2026 - Ticket Deal Finding & Price Comparison Engine
-- ============================================================================

DROP SCHEMA IF EXISTS deal_finder_schema CASCADE;
CREATE SCHEMA deal_finder_schema;

-- ============================================================================
-- PROVIDERS TABLE
-- Registry of ticket providers with trust scores and fee info
-- ============================================================================
CREATE TABLE deal_finder_schema.providers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(150) NOT NULL,
    logo_url VARCHAR(500),
    website_url VARCHAR(500) NOT NULL,
    trust_score INTEGER NOT NULL DEFAULT 50,
    fee_percentage DECIMAL(5, 2) DEFAULT 0.00,
    has_buyer_protection BOOLEAN DEFAULT FALSE,
    api_type VARCHAR(30) NOT NULL DEFAULT 'SIMULATED',
    api_base_url VARCHAR(500),
    api_key_env_var VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_providers_name ON deal_finder_schema.providers(name);
CREATE INDEX idx_providers_active ON deal_finder_schema.providers(is_active);

-- ============================================================================
-- PRICE SNAPSHOTS TABLE
-- Core table: every price observation from every provider
-- ============================================================================
CREATE TABLE deal_finder_schema.price_snapshots (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL REFERENCES deal_finder_schema.providers(id),
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    base_price DECIMAL(10, 2) NOT NULL,
    fee_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_price DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    availability_status VARCHAR(30) DEFAULT 'AVAILABLE',
    quantity_available INTEGER,
    booking_url TEXT NOT NULL,
    source_type VARCHAR(20) NOT NULL DEFAULT 'SIMULATED',
    fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_snapshots_match ON deal_finder_schema.price_snapshots(match_id);
CREATE INDEX idx_snapshots_provider ON deal_finder_schema.price_snapshots(provider_id);
CREATE INDEX idx_snapshots_match_cat ON deal_finder_schema.price_snapshots(match_id, category);
CREATE INDEX idx_snapshots_match_provider ON deal_finder_schema.price_snapshots(match_id, provider_id);
CREATE INDEX idx_snapshots_fetched ON deal_finder_schema.price_snapshots(fetched_at);
CREATE INDEX idx_snapshots_total_price ON deal_finder_schema.price_snapshots(total_price);

-- ============================================================================
-- DEAL SCORES TABLE
-- Pre-computed deal quality per match+provider+category
-- ============================================================================
CREATE TABLE deal_finder_schema.deal_scores (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL REFERENCES deal_finder_schema.providers(id),
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    deal_score INTEGER NOT NULL DEFAULT 50,
    current_price DECIMAL(10, 2) NOT NULL,
    market_average DECIMAL(10, 2) NOT NULL,
    savings_percentage DECIMAL(5, 2) DEFAULT 0.00,
    price_trend VARCHAR(20) DEFAULT 'STABLE',
    trend_percentage DECIMAL(5, 2) DEFAULT 0.00,
    price_7d_low DECIMAL(10, 2),
    price_7d_high DECIMAL(10, 2),
    best_time_to_buy VARCHAR(30) DEFAULT 'NOW',
    recommendation TEXT,
    booking_url TEXT NOT NULL,
    last_computed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_deal_match_provider_cat UNIQUE (match_id, provider_id, category)
);

CREATE INDEX idx_deals_match ON deal_finder_schema.deal_scores(match_id);
CREATE INDEX idx_deals_score ON deal_finder_schema.deal_scores(deal_score DESC);
CREATE INDEX idx_deals_match_cat ON deal_finder_schema.deal_scores(match_id, category);
CREATE INDEX idx_deals_trend ON deal_finder_schema.deal_scores(price_trend);

-- ============================================================================
-- MATCH DEAL SUMMARY TABLE
-- Aggregated per-match statistics for quick queries
-- ============================================================================
CREATE TABLE deal_finder_schema.match_deal_summary (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    lowest_price DECIMAL(10, 2),
    highest_price DECIMAL(10, 2),
    average_price DECIMAL(10, 2),
    best_provider_id BIGINT REFERENCES deal_finder_schema.providers(id),
    best_deal_score INTEGER,
    num_providers INTEGER DEFAULT 0,
    overall_trend VARCHAR(20) DEFAULT 'STABLE',
    best_time_to_buy VARCHAR(30) DEFAULT 'NOW',
    last_computed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_summary_match_cat UNIQUE (match_id, category)
);

CREATE INDEX idx_summary_match ON deal_finder_schema.match_deal_summary(match_id);
CREATE INDEX idx_summary_lowest ON deal_finder_schema.match_deal_summary(lowest_price);

-- ============================================================================
-- FETCH LOG TABLE
-- Audit trail for price fetching operations
-- ============================================================================
CREATE TABLE deal_finder_schema.fetch_log (
    id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT REFERENCES deal_finder_schema.providers(id),
    fetch_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    records_fetched INTEGER DEFAULT 0,
    error_message TEXT,
    duration_ms BIGINT,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

CREATE INDEX idx_fetch_log_provider ON deal_finder_schema.fetch_log(provider_id);
CREATE INDEX idx_fetch_log_started ON deal_finder_schema.fetch_log(started_at);

-- ============================================================================
-- SEED DATA - 10 Ticket Providers
-- ============================================================================
INSERT INTO deal_finder_schema.providers
(name, display_name, website_url, trust_score, fee_percentage, has_buyer_protection, api_type, priority) VALUES
('FIFA Official', 'FIFA Official', 'https://www.fifa.com/tickets', 95, 0.00, TRUE, 'SIMULATED', 1),
('Ticketmaster', 'Ticketmaster', 'https://www.ticketmaster.com', 85, 18.00, TRUE, 'REAL_API', 2),
('StubHub', 'StubHub', 'https://www.stubhub.com', 82, 15.00, TRUE, 'SIMULATED', 3),
('SeatGeek', 'SeatGeek', 'https://seatgeek.com', 80, 12.00, TRUE, 'REAL_API', 4),
('Viagogo', 'Viagogo', 'https://www.viagogo.com', 55, 25.00, FALSE, 'SIMULATED', 5),
('VividSeats', 'Vivid Seats', 'https://www.vividseats.com', 75, 14.00, TRUE, 'SIMULATED', 6),
('TickPick', 'TickPick', 'https://www.tickpick.com', 78, 0.00, TRUE, 'SIMULATED', 7),
('GameTime', 'Gametime', 'https://gametime.co', 72, 10.00, TRUE, 'SIMULATED', 8),
('TicketCity', 'TicketCity', 'https://www.ticketcity.com', 68, 16.00, TRUE, 'SIMULATED', 9),
('AXS', 'AXS', 'https://www.axs.com', 70, 12.00, FALSE, 'SIMULATED', 10);

-- ============================================================================
-- GRANT PERMISSIONS
-- ============================================================================
GRANT USAGE ON SCHEMA deal_finder_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA deal_finder_schema TO worldcup_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA deal_finder_schema TO worldcup_user;

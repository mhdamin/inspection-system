-- ========================================
-- FleetGuard Database Initialization Script
-- ========================================
-- This script runs automatically when the PostgreSQL container
-- is created for the first time.
--
-- It creates:
-- - Database extensions
-- - Schema
-- - Initial grants
-- ========================================

-- Enable UUID extension for primary keys
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pg_trgm for faster text search (optional, for future use)
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create schema
CREATE SCHEMA IF NOT EXISTS fleetguard;

-- Grant privileges to the fleetguard user
GRANT ALL PRIVILEGES ON SCHEMA fleetguard TO fleetguard_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA fleetguard TO fleetguard_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA fleetguard TO fleetguard_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA fleetguard GRANT ALL ON TABLES TO fleetguard_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA fleetguard GRANT ALL ON SEQUENCES TO fleetguard_user;

-- Create a simple health check table (optional)
CREATE TABLE IF NOT EXISTS health_check (
    id SERIAL PRIMARY KEY,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial health check record
INSERT INTO health_check (checked_at) VALUES (CURRENT_TIMESTAMP);

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE 'FleetGuard database initialized successfully!';
    RAISE NOTICE 'Extensions enabled: uuid-ossp, pg_trgm';
    RAISE NOTICE 'Schema created: fleetguard';
END $$;

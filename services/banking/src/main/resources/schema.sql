CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS bank_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_holder_id UUID NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
)
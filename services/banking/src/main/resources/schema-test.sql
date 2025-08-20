-- For H2 database (test environment)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP,
    blacklisted BOOLEAN
);

-- INSERT INTO users (id, name, email, password, role, created_at, blacklisted) 
-- VALUES (RANDOM_UUID(), 'Admin User', 'admin@example.com', 'encoded_password', 'ADMIN', CURRENT_TIMESTAMP, false);

CREATE TABLE IF NOT EXISTS bank_transactions (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    account_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES bank_accounts(id),
    description TEXT
);
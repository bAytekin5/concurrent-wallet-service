CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id            UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL UNIQUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallets
(
    id         UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    user_id    UUID           NOT NULL REFERENCES users (id),
    currency   VARCHAR(3)     NOT NULL,
    balance    DECIMAL(19, 4) NOT NULL  DEFAULT 0.0000,
    version    BIGINT         NOT NULL  DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, currency)
);
CREATE TABLE transactions
(
    id                 UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    sender_wallet_id   UUID REFERENCES wallets (id),
    receiver_wallet_id UUID REFERENCES wallets (id),
    amount             DECIMAL(19, 4) NOT NULL,
    transaction_type   VARCHAR(20)    NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    description        VARCHAR(255),
    transaction_date   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallets_user_id ON wallets (user_id);
CREATE INDEX idx_transactions_sender ON transactions (sender_wallet_id);
CREATE INDEX idx_transactions_receiver ON transactions (receiver_wallet_id);
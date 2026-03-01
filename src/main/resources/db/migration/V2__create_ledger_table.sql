CREATE TABLE ledger_entries
(
    id             UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    transaction_id UUID           NOT NULL REFERENCES transactions (id),
    wallet_id      UUID           NOT NULL,
    entry_type     VARCHAR(20)    NOT NULL,
    amount         DECIMAL(19, 4) NOT NULL,
    currency       VARCHAR(3)     NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ledger_wallet_id ON ledger_entries (wallet_id);
CREATE INDEX idx_ledger_transaction_id ON ledger_entries (transaction_id);


CREATE INDEX idx_ledger_wallet_created ON ledger_entries (wallet_id, created_at DESC);
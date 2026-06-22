CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    external_id VARCHAR(255) NOT NULL,
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    is_flagged BOOLEAN NOT NULL DEFAULT false,
    first_seen_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_seen_at TIMESTAMP,
    UNIQUE(tenant_id, external_id)
);
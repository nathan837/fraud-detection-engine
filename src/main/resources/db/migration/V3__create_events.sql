CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES users(id),
    event_type VARCHAR(20) NOT NULL,
    device_id VARCHAR(255),
    ip_address VARCHAR(50),
    country VARCHAR(10),
    city VARCHAR(100),
    user_agent TEXT,
    amount DECIMAL(19,4),
    currency VARCHAR(10),
    recipient_id VARCHAR(255),
    raw_payload JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_events_tenant_id ON events(tenant_id);
CREATE INDEX idx_events_user_id ON events(user_id);
CREATE INDEX idx_events_created_at ON events(created_at);
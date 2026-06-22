CREATE TABLE decisions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL UNIQUE REFERENCES events(id),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES users(id),
    risk_score INTEGER NOT NULL,
    decision VARCHAR(20) NOT NULL,
    triggered_rules JSONB,
    response_ms INTEGER,
    reviewed_by VARCHAR(255),
    review_outcome VARCHAR(50),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_decisions_tenant_id ON decisions(tenant_id);
CREATE INDEX idx_decisions_user_id ON decisions(user_id);
CREATE INDEX idx_decisions_decision ON decisions(decision);
CREATE INDEX idx_decisions_created_at ON decisions(created_at);
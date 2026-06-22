CREATE TABLE rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    condition_type VARCHAR(100) NOT NULL,
    condition_value JSONB,
    score INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rules_tenant_id ON rules(tenant_id);
CREATE INDEX idx_rules_is_active ON rules(is_active);
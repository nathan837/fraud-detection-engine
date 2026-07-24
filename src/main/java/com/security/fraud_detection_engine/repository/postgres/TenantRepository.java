package com.security.fraud_detection_engine.repository.postgres;

import com.security.fraud_detection_engine.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    // Find tenant by API key
    // Used on every single request to authenticate the caller
    Optional<Tenant> findByApiKey(String apiKey);

    // Find tenant by name
    // Used in the dashboard to look up a customer
    Optional<Tenant> findByName(String name);

    // Check if a tenant is active
    // Disabled tenants should be rejected immediately
    Optional<Tenant> findByApiKeyAndIsActiveTrue(String apiKey);
}
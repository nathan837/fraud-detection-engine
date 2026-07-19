package com.security.fraud_detection_engine.repository.postgres;

import com.security.fraud_detection_engine.domain.FraudRule;
import com.security.fraud_detection_engine.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<FraudRule, UUID> {

    @Query("SELECT r FROM FraudRule r " +
           "WHERE r.isActive = true " +
           "AND (r.tenant IS NULL OR r.tenant = :tenant)")
    List<FraudRule> findActiveRulesForTenant(@Param("tenant") Tenant tenant);

    // Load only global rules — apply to all tenants
    List<FraudRule> findByTenantIsNullAndIsActiveTrue();

    // Load tenant specific rules only
    List<FraudRule> findByTenantAndIsActiveTrue(Tenant tenant);
}
package com.security.fraud_detection_engine.repository.postgressitory.postgres;

import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.User;
import com.security.fraud_detection_engine.domain.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    // Find user by tenant + their ID in Chapa's system
    // This is the main lookup on every transaction
    Optional<User> findByTenantAndExternalId(Tenant tenant, String externalId);

    // Find all flagged users for a tenant
    // Used in the dashboard to show high risk users
    List<User> findByTenantAndIsFlaggedTrue(Tenant tenant);

    // Find users by risk level
    // Used for reporting and monitoring
    List<User> findByTenantAndRiskLevel(Tenant tenant, RiskLevel riskLevel);
}
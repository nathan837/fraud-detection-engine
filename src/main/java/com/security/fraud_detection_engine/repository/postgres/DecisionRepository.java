package com.security.fraud_detection_engine.repository.postgres;

import com.security.fraud_detection_engine.domain.Decision;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.User;
import com.security.fraud_detection_engine.domain.enums.DecisionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {

    // Find decision by event — one decision per event
    Optional<Decision> findByEventId(UUID eventId);

    // All decisions for a user — used in investigation
    List<Decision> findByUserOrderByCreatedAtDesc(User user);

    // All blocked decisions for a tenant — used in dashboard
    List<Decision> findByTenantAndDecisionOrderByCreatedAtDesc(
        Tenant tenant,
        DecisionType decision
    );

    // Decisions needing review — unreviewed blocks
    List<Decision> findByTenantAndDecisionAndReviewedByIsNull(
        Tenant tenant,
        DecisionType decision
    );

    // Count decisions by type in a time window
    // Used for dashboard metrics
    @Query("SELECT COUNT(d) FROM Decision d " +
           "WHERE d.tenant = :tenant " +
           "AND d.decision = :decision " +
           "AND d.createdAt > :since")
    long countByTenantAndDecisionSince(
        @Param("tenant") Tenant tenant,
        @Param("decision") DecisionType decision,
        @Param("since") LocalDateTime since
    );
}
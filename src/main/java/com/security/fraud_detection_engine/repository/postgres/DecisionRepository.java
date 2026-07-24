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

    Optional<Decision> findByEventId(UUID eventId);

    List<Decision> findByUserOrderByCreatedAtDesc(User user);

    List<Decision> findByTenantOrderByCreatedAtDesc(Tenant tenant);

    List<Decision> findByTenantAndDecisionOrderByCreatedAtDesc(
        Tenant tenant, DecisionType decision);

    List<Decision> findByTenantAndDecisionAndReviewedByIsNull(
        Tenant tenant, DecisionType decision);

    @Query("SELECT COUNT(d) FROM Decision d " +
           "WHERE d.tenant = :tenant " +
           "AND d.decision = :decision " +
           "AND d.createdAt > :since")
    long countByTenantAndDecisionSince(
        @Param("tenant") Tenant tenant,
        @Param("decision") DecisionType decision,
        @Param("since") LocalDateTime since);
}

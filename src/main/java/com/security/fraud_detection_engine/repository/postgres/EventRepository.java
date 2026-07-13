package com.security.fraud_detection_engine.repository.postgres;

import com.security.fraud_detection_engine.domain.FraudEvent;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.User;
import com.security.fraud_detection_engine.domain.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<FraudEvent, UUID> {

    List<FraudEvent> findByUserOrderByCreatedAtDesc(User user);

    List<FraudEvent> findByUserAndEventTypeAndCreatedAtAfter(
        User user,
        EventType eventType,
        LocalDateTime after
    );

    @Query("SELECT AVG(e.amount) FROM FraudEvent e " +
           "WHERE e.user = :user " +
           "AND e.eventType = 'TRANSACTION' " +
           "AND e.createdAt > :since")
    BigDecimal findAverageAmountByUserSince(
        @Param("user") User user,
        @Param("since") LocalDateTime since
    );

    boolean existsByUserAndRecipientId(User user, String recipientId);

    List<FraudEvent> findByTenantOrderByCreatedAtDesc(Tenant tenant);
}

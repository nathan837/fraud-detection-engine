package com.security.fraud_detection_engine.repository.postgressitory.postgres;

import com.security.fraud_detection_engine.domain.Device;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepo extends JpaRepository<Device, UUID> {

    // Check if this device is known for this user
    // Core check for new device rule
    Optional<Device> findByUserAndDeviceId(User user, String deviceId);

    // Is this device known? Simple boolean check
    boolean existsByUserAndDeviceId(User user, String deviceId);

    // All devices for a user — used in dashboard investigation
    List<Device> findByUserOrderByLastSeenAtDesc(User user);

    // All trusted devices for a user
    List<Device> findByUserAndIsTrustedTrue(User user);

    // All devices seen across a tenant
    // Used for fraud ring detection later
    List<Device> findByTenant(Tenant tenant);
}
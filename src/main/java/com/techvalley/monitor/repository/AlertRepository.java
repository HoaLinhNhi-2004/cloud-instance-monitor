package com.techvalley.monitor.repository;

import com.techvalley.monitor.domain.Alert;
import com.techvalley.monitor.domain.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByInstanceId(Long instanceId);
    List<Alert> findAllByIsResolved(Boolean isResolved);

    // dùng cho alert dedupe (business rule #2)
    Optional<Alert> findByInstanceIdAndAlertTypeAndIsResolved(Long instanceId, AlertType alertType, Boolean isResolved);
}

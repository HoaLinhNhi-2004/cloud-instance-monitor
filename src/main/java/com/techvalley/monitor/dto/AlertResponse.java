package com.techvalley.monitor.dto;

import com.techvalley.monitor.domain.Alert;
import com.techvalley.monitor.domain.enums.AlertType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlertResponse {

    private final Long id;
    private final Long instanceId;
    private final String instanceName;
    private final AlertType alertType;
    private final String message;
    private final Boolean isResolved;
    private final LocalDateTime detectedAt;
    private final LocalDateTime resolvedAt;

    public AlertResponse(Alert alert) {
        this.id = alert.getId();
        this.instanceId = alert.getInstance().getId();
        this.instanceName = alert.getInstance().getInstanceName();
        this.alertType = alert.getAlertType();
        this.message = alert.getMessage();
        this.isResolved = alert.getIsResolved();
        this.detectedAt = alert.getDetectedAt();
        this.resolvedAt = alert.getResolvedAt();
    }
}

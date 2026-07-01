package com.techvalley.monitor.dto;

import com.techvalley.monitor.domain.Instance;
import com.techvalley.monitor.domain.enums.InstanceStatus;
import com.techvalley.monitor.domain.enums.InstanceType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class InstanceResponse {

    private final Long id;
    private final String instanceName;
    private final String region;
    private final InstanceType instanceType;
    private final InstanceStatus status;
    private final Integer cpuUsage;
    private final BigDecimal monthlyCost;
    private final Long clientId;
    private final String clientName;
    private final LocalDateTime launchedAt;
    private final LocalDateTime updatedAt;

    public InstanceResponse(Instance instance) {
        this.id = instance.getId();
        this.instanceName = instance.getInstanceName();
        this.region = instance.getRegion();
        this.instanceType = instance.getInstanceType();
        this.status = instance.getStatus();
        this.cpuUsage = instance.getCpuUsage();
        this.monthlyCost = instance.getMonthlyCost();
        this.clientId = instance.getClient().getId();
        this.clientName = instance.getClient().getClientName();
        this.launchedAt = instance.getLaunchedAt();
        this.updatedAt = instance.getUpdatedAt();
    }
}

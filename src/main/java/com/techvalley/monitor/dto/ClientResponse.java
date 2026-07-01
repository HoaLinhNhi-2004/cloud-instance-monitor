package com.techvalley.monitor.dto;

import com.techvalley.monitor.domain.Client;
import com.techvalley.monitor.domain.enums.ContractPlan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClientResponse {

    private final Long id;
    private final String clientName;
    private final ContractPlan contractPlan;
    private final Long managerId;
    private final String managerName;
    private final LocalDateTime createdAt;

    public ClientResponse(Client client) {
        this.id = client.getId();
        this.clientName = client.getClientName();
        this.contractPlan = client.getContractPlan();
        this.managerId = client.getManager().getId();
        this.managerName = client.getManager().getName();
        this.createdAt = client.getCreatedAt();
    }
}

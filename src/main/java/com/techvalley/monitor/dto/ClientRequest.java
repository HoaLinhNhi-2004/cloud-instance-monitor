package com.techvalley.monitor.dto;

import com.techvalley.monitor.domain.enums.ContractPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClientRequest {

    @NotBlank
    private String clientName;

    @NotNull
    private ContractPlan contractPlan;

    @NotNull
    private Long managerId;
}

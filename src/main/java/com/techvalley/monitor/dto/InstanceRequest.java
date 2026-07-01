package com.techvalley.monitor.dto;

import com.techvalley.monitor.domain.enums.InstanceStatus;
import com.techvalley.monitor.domain.enums.InstanceType;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class InstanceRequest {

    @NotBlank
    private String instanceName;

    @NotBlank
    private String region;

    @NotNull
    private InstanceType instanceType;

    @NotNull
    private InstanceStatus status;

    @NotNull
    @Min(0) @Max(100)
    private Integer cpuUsage;

    @NotNull
    private Long clientId;
}

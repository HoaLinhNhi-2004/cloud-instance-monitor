package com.techvalley.monitor.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MonitorReportResponse {

    private final int totalInstances;
    private final Map<String, Long> countByStatus;
    private final List<AlertResponse> warnings;
    private final List<AlertResponse> errors;
    private final List<AlertResponse> longStopped;

    public MonitorReportResponse(int totalInstances, Map<String, Long> countByStatus,
                                  List<AlertResponse> warnings, List<AlertResponse> errors,
                                  List<AlertResponse> longStopped) {
        this.totalInstances = totalInstances;
        this.countByStatus = countByStatus;
        this.warnings = warnings;
        this.errors = errors;
        this.longStopped = longStopped;
    }
}

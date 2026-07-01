package com.techvalley.monitor.controller;

import com.techvalley.monitor.common.ApiResponse;
import com.techvalley.monitor.dto.AlertResponse;
import com.techvalley.monitor.dto.MonitorReportResponse;
import com.techvalley.monitor.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping("/warnings")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> warnings(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(monitorService.getWarnings(user.getUsername())));
    }

    @GetMapping("/errors")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> errors(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(monitorService.getErrors(user.getUsername())));
    }

    @GetMapping("/long-stopped")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> longStopped(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(monitorService.getLongStopped(user.getUsername())));
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<MonitorReportResponse>> report(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(monitorService.getReport(user.getUsername())));
    }
}

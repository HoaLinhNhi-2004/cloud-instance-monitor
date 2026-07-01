package com.techvalley.monitor.controller;

import com.techvalley.monitor.common.ApiResponse;
import com.techvalley.monitor.dto.InstanceRequest;
import com.techvalley.monitor.dto.InstanceResponse;
import com.techvalley.monitor.service.InstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instances")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceService instanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstanceResponse>>> getAll(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(instanceService.getAll(user.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstanceResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(instanceService.getById(id, user.getUsername())));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InstanceResponse>> create(
            @Valid @RequestBody InstanceRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(instanceService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InstanceResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody InstanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(instanceService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        instanceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Instance deleted", null));
    }
}

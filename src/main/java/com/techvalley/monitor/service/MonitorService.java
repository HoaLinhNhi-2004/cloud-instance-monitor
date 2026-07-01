package com.techvalley.monitor.service;

import com.techvalley.monitor.domain.Alert;
import com.techvalley.monitor.domain.Instance;
import com.techvalley.monitor.domain.InstanceStatusLog;
import com.techvalley.monitor.domain.Member;
import com.techvalley.monitor.domain.enums.AlertType;
import com.techvalley.monitor.domain.enums.InstanceStatus;
import com.techvalley.monitor.domain.enums.MemberRole;
import com.techvalley.monitor.dto.AlertResponse;
import com.techvalley.monitor.dto.MonitorReportResponse;
import com.techvalley.monitor.exception.ResourceNotFoundException;
import com.techvalley.monitor.repository.AlertRepository;
import com.techvalley.monitor.repository.InstanceRepository;
import com.techvalley.monitor.repository.InstanceStatusLogRepository;
import com.techvalley.monitor.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private static final int CPU_HIGH_THRESHOLD = 80;
    private static final long LONG_STOPPED_HOURS = 72;

    private final InstanceRepository instanceRepository;
    private final InstanceStatusLogRepository statusLogRepository;
    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<AlertResponse> getWarnings(String currentEmail) {
        return detectWarnings(scopedInstances(currentEmail));
    }

    @Transactional
    public List<AlertResponse> getErrors(String currentEmail) {
        return detectErrors(scopedInstances(currentEmail));
    }

    @Transactional
    public List<AlertResponse> getLongStopped(String currentEmail) {
        return detectLongStopped(scopedInstances(currentEmail));
    }

    @Transactional
    public MonitorReportResponse getReport(String currentEmail) {
        List<Instance> scoped = scopedInstances(currentEmail);
        Map<String, Long> countByStatus = scoped.stream()
                .collect(Collectors.groupingBy(i -> i.getStatus().name(), Collectors.counting()));

        return new MonitorReportResponse(
                scoped.size(),
                countByStatus,
                detectWarnings(scoped),
                detectErrors(scoped),
                detectLongStopped(scoped)
        );
    }

    private List<AlertResponse> detectWarnings(List<Instance> scoped) {
        List<Instance> candidates = scoped.stream()
                .filter(i -> i.getCpuUsage() >= CPU_HIGH_THRESHOLD)
                .toList();
        candidates.forEach(instance -> createIfAbsent(instance, AlertType.CPU_HIGH,
                "CPU usage " + instance.getCpuUsage() + "% exceeds threshold " + CPU_HIGH_THRESHOLD + "%"));
        return activeAlerts(candidates, AlertType.CPU_HIGH);
    }

    private List<AlertResponse> detectErrors(List<Instance> scoped) {
        List<Instance> candidates = scoped.stream()
                .filter(i -> i.getStatus() == InstanceStatus.ERROR)
                .toList();
        candidates.forEach(instance -> createIfAbsent(instance, AlertType.ERROR_DETECTED,
                "Instance is in ERROR status"));
        return activeAlerts(candidates, AlertType.ERROR_DETECTED);
    }

    private List<AlertResponse> detectLongStopped(List<Instance> scoped) {
        List<Instance> candidates = scoped.stream()
                .filter(i -> i.getStatus() == InstanceStatus.STOPPED)
                .filter(this::isLongStopped)
                .toList();
        candidates.forEach(instance -> createIfAbsent(instance, AlertType.LONG_STOPPED,
                "Instance has been STOPPED for " + hoursSinceStatusChange(instance)
                        + " hours (threshold " + LONG_STOPPED_HOURS + "h)"));
        return activeAlerts(candidates, AlertType.LONG_STOPPED);
    }

    private boolean isLongStopped(Instance instance) {
        return hoursSinceStatusChange(instance) >= LONG_STOPPED_HOURS;
    }

    private long hoursSinceStatusChange(Instance instance) {
        LocalDateTime changedAt = statusLogRepository
                .findTopByInstanceIdOrderByChangedAtDesc(instance.getId())
                .map(InstanceStatusLog::getChangedAt)
                .orElseThrow();
        return Duration.between(changedAt, LocalDateTime.now()).toHours();
    }

    private void createIfAbsent(Instance instance, AlertType type, String message) {
        boolean exists = alertRepository
                .findByInstanceIdAndAlertTypeAndIsResolved(instance.getId(), type, false)
                .isPresent();
        if (exists) return;

        Alert alert = new Alert();
        alert.setInstance(instance);
        alert.setAlertType(type);
        alert.setMessage(message);
        alertRepository.save(alert);
    }

    private List<AlertResponse> activeAlerts(List<Instance> instances, AlertType type) {
        return instances.stream()
                .map(instance -> alertRepository
                        .findByInstanceIdAndAlertTypeAndIsResolved(instance.getId(), type, false)
                        .orElseThrow())
                .map(AlertResponse::new)
                .toList();
    }

    private List<Instance> scopedInstances(String email) {
        Member current = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + email));
        return (current.getRole() == MemberRole.ADMIN)
                ? instanceRepository.findAll()
                : instanceRepository.findAllByClientManagerId(current.getId());
    }
}

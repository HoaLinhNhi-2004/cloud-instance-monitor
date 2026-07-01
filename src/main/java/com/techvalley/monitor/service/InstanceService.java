package com.techvalley.monitor.service;

import com.techvalley.monitor.domain.Client;
import com.techvalley.monitor.domain.Instance;
import com.techvalley.monitor.domain.InstanceStatusLog;
import com.techvalley.monitor.domain.Member;
import com.techvalley.monitor.domain.enums.InstanceStatus;
import com.techvalley.monitor.domain.enums.InstanceType;
import com.techvalley.monitor.domain.enums.MemberRole;
import com.techvalley.monitor.dto.InstanceRequest;
import com.techvalley.monitor.dto.InstanceResponse;
import com.techvalley.monitor.exception.ActiveInstanceException;
import com.techvalley.monitor.exception.ForbiddenException;
import com.techvalley.monitor.exception.ResourceNotFoundException;
import com.techvalley.monitor.repository.AlertRepository;
import com.techvalley.monitor.repository.ClientRepository;
import com.techvalley.monitor.repository.InstanceRepository;
import com.techvalley.monitor.repository.InstanceStatusLogRepository;
import com.techvalley.monitor.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private static final Map<InstanceType, BigDecimal> MONTHLY_COST_MAP = Map.of(
            InstanceType.SMALL, new BigDecimal("50.00"),
            InstanceType.MEDIUM, new BigDecimal("120.00"),
            InstanceType.LARGE, new BigDecimal("250.00")
    );

    private final InstanceRepository instanceRepository;
    private final ClientRepository clientRepository;
    private final MemberRepository memberRepository;
    private final InstanceStatusLogRepository statusLogRepository;
    private final AlertRepository alertRepository;

    public List<InstanceResponse> getAll(String currentEmail) {
        Member current = loadMember(currentEmail);
        List<Instance> instances = (current.getRole() == MemberRole.ADMIN)
                ? instanceRepository.findAll()
                : instanceRepository.findAllByClientManagerId(current.getId());
        return instances.stream().map(InstanceResponse::new).toList();
    }

    public InstanceResponse getById(Long id, String currentEmail) {
        Instance instance = findOrThrow(id);
        checkAccess(instance, currentEmail);
        return new InstanceResponse(instance);
    }

    @Transactional
    public InstanceResponse create(InstanceRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + request.getClientId()));

        Instance instance = new Instance();
        instance.setInstanceName(request.getInstanceName());
        instance.setRegion(request.getRegion());
        instance.setInstanceType(request.getInstanceType());
        instance.setStatus(request.getStatus());
        instance.setCpuUsage(request.getCpuUsage());
        instance.setMonthlyCost(MONTHLY_COST_MAP.get(request.getInstanceType()));
        instance.setClient(client);

        Instance saved = instanceRepository.save(instance);
        logStatusChange(saved, saved.getStatus());
        return new InstanceResponse(saved);
    }

    @Transactional
    public InstanceResponse update(Long id, InstanceRequest request) {
        Instance instance = findOrThrow(id);
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + request.getClientId()));

        InstanceStatus oldStatus = instance.getStatus();

        instance.setInstanceName(request.getInstanceName());
        instance.setRegion(request.getRegion());
        instance.setInstanceType(request.getInstanceType());
        instance.setStatus(request.getStatus());
        instance.setCpuUsage(request.getCpuUsage());
        instance.setMonthlyCost(MONTHLY_COST_MAP.get(request.getInstanceType()));
        instance.setClient(client);

        Instance saved = instanceRepository.save(instance);

        // rule #6: ghi log khi status thay đổi
        if (oldStatus != request.getStatus()) {
            logStatusChange(saved, request.getStatus());
        }

        return new InstanceResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Instance instance = findOrThrow(id);

        // rule #5: không xóa instance đang RUNNING
        if (instance.getStatus() == InstanceStatus.RUNNING) {
            throw new ActiveInstanceException(
                    "Cannot delete instance '" + instance.getInstanceName() + "' while it is RUNNING");
        }

        // xóa child records trước để tránh FK constraint violation
        statusLogRepository.deleteAllByInstanceId(id);
        alertRepository.deleteAllByInstanceId(id);
        instanceRepository.deleteById(id);
    }

    private void logStatusChange(Instance instance, InstanceStatus status) {
        InstanceStatusLog log = new InstanceStatusLog();
        log.setInstance(instance);
        log.setStatus(status);
        statusLogRepository.save(log);
    }

    private Instance findOrThrow(Long id) {
        return instanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instance not found: " + id));
    }

    private Member loadMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + email));
    }

    private void checkAccess(Instance instance, String currentEmail) {
        Member current = loadMember(currentEmail);
        if (current.getRole() == MemberRole.CLIENT_MANAGER
                && !instance.getClient().getManager().getId().equals(current.getId())) {
            throw new ForbiddenException("You do not have access to this instance");
        }
    }
}

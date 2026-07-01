package com.techvalley.monitor.repository;

import com.techvalley.monitor.domain.Instance;
import com.techvalley.monitor.domain.enums.InstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    List<Instance> findAllByClientId(Long clientId);
    List<Instance> findAllByStatus(InstanceStatus status);
    List<Instance> findAllByClientIdAndStatus(Long clientId, InstanceStatus status);
    // CLIENT_MANAGER access: instances thuộc các client mà member này quản lý
    List<Instance> findAllByClientManagerId(Long managerId);
}

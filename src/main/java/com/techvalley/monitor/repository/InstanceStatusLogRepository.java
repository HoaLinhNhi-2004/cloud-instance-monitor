package com.techvalley.monitor.repository;

import com.techvalley.monitor.domain.InstanceStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InstanceStatusLogRepository extends JpaRepository<InstanceStatusLog, Long> {
    // dùng cho tính SLA: lấy toàn bộ log của instance trong khoảng thời gian
    List<InstanceStatusLog> findAllByInstanceIdAndChangedAtBetweenOrderByChangedAtAsc(
            Long instanceId, LocalDateTime from, LocalDateTime to);
}

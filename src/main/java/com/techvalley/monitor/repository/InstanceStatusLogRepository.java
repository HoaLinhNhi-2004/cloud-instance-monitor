package com.techvalley.monitor.repository;

import com.techvalley.monitor.domain.InstanceStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InstanceStatusLogRepository extends JpaRepository<InstanceStatusLog, Long> {
    // dùng cho tính SLA: lấy toàn bộ log của instance trong khoảng thời gian
    List<InstanceStatusLog> findAllByInstanceIdAndChangedAtBetweenOrderByChangedAtAsc(
            Long instanceId, LocalDateTime from, LocalDateTime to);

    // dùng cho long-stopped: thời điểm instance chuyển sang trạng thái hiện tại
    Optional<InstanceStatusLog> findTopByInstanceIdOrderByChangedAtDesc(Long instanceId);

    void deleteAllByInstanceId(Long instanceId);
}

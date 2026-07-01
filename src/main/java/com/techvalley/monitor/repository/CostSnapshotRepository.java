package com.techvalley.monitor.repository;

import com.techvalley.monitor.domain.CostSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CostSnapshotRepository extends JpaRepository<CostSnapshot, Long> {
    List<CostSnapshot> findAllByClientIdOrderBySnapshotMonthDesc(Long clientId);
    Optional<CostSnapshot> findByClientIdAndSnapshotMonth(Long clientId, String snapshotMonth);
}

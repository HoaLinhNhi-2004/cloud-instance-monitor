package com.techvalley.monitor.domain;

import com.techvalley.monitor.domain.enums.InstanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "instance_status_logs")
@Getter
@Setter
@NoArgsConstructor
public class InstanceStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private Instance instance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstanceStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt = LocalDateTime.now();
}

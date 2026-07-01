package com.techvalley.monitor.domain;

import com.techvalley.monitor.domain.enums.InstanceStatus;
import com.techvalley.monitor.domain.enums.InstanceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "instances")
@Getter
@Setter
@NoArgsConstructor
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instanceName;

    @Column(nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstanceType instanceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstanceStatus status;

    @Column(nullable = false)
    private Integer cpuUsage;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, updatable = false)
    private LocalDateTime launchedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

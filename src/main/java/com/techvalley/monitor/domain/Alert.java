package com.techvalley.monitor.domain;

import com.techvalley.monitor.domain.enums.AlertType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private Instance instance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean isResolved = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;
}

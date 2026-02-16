package com.tracking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "effort_logs")
@Data
public class EffortLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "log_date")
    private LocalDate logDate;

    @Column(name = "hours_logged")
    private BigDecimal hoursLogged;

    private String description;
}

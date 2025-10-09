package com.techtorque.time_logging_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, updatable = false)
  private String employeeId;

  @Column(nullable = false, updatable = false)
  private String serviceId; // Can also be a projectId

  @Column(nullable = false)
  private double hours;

  @Column(nullable = false)
  private LocalDate date; // The date the work was performed

  @Lob
  private String description;

  private String workType;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
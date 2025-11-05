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

  // Added nullable projectId so a time log can be associated with either a project or a service
  @Column(nullable = true)
  private String projectId;



  @Column(nullable = false)
  private double hours;

  @Column(nullable = false)
  private LocalDate date; // The date the work was performed

  @Column(columnDefinition = "TEXT")
  private String description;

  private String workType;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  // Explicit getters - Lombok @Data should generate these, but we make them explicit
  public String getId() {
    return id;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public String getServiceId() {
    return serviceId;
  }

  public String getProjectId() {
    return projectId;
  }

  public double getHours() {
    return hours;
  }

  public LocalDate getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  public String getWorkType() {
    return workType;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  // Explicit setters - Lombok @Data should generate these, but we make them explicit
  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public void setHours(double hours) {
    this.hours = hours;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setWorkType(String workType) {
    this.workType = workType;
  }
}

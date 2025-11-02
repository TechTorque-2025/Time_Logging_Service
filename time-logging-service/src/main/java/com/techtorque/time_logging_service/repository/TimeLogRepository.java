package com.techtorque.time_logging_service.repository;

import com.techtorque.time_logging_service.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, String> {

  List<TimeLog> findByEmployeeId(String employeeId);

  List<TimeLog> findByServiceId(String serviceId);

  List<TimeLog> findByProjectId(String projectId);

  Optional<TimeLog> findByIdAndEmployeeId(String id, String employeeId);

  List<TimeLog> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT SUM(t.hours) FROM TimeLog t WHERE t.employeeId = :employeeId")
  Double getTotalHoursByEmployeeId(String employeeId);
}

package com.techtorque.time_logging_service.repository;

import com.techtorque.time_logging_service.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, String> {

  // For an employee to get their own logs
  List<TimeLog> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

  // To get all time logs associated with a specific service or project
  List<TimeLog> findByServiceId(String serviceId);

  // For security: find a log by ID only if it belongs to the specified employee
  Optional<TimeLog> findByIdAndEmployeeId(String id, String employeeId);

  // Get all logs for an employee
  List<TimeLog> findByEmployeeIdOrderByDateDesc(String employeeId);
}
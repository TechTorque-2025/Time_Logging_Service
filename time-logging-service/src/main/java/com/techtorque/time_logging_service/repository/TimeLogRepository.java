package com.techtorque.time_logging_service.repository;

import com.techtorque.time_logging_service.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, String> {

  // For an employee to get their own logs
  List<TimeLog> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

  // To get all time logs associated with a specific service or project
  List<TimeLog> findByServiceId(String serviceId);
}
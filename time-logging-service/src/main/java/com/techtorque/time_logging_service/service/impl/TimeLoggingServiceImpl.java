package com.techtorque.time_logging_service.service.impl;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import com.techtorque.time_logging_service.service.TimeLoggingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;


@Service
@Transactional
public class TimeLoggingServiceImpl implements TimeLoggingService {

  private final TimeLogRepository timeLogRepository;

  public TimeLoggingServiceImpl(TimeLogRepository timeLogRepository) {
    this.timeLogRepository = timeLogRepository;
  }

  @Override
  public TimeLog logWorkTime(TimeLogRequest request, String employeeId) {
    // Create a new time log entry from the request
    TimeLog timeLog = new TimeLog();
    timeLog.setEmployeeId(employeeId);
    timeLog.setServiceId(request.getServiceId());
    timeLog.setProjectId(request.getProjectId());
    timeLog.setHours(request.getHours());
    timeLog.setDate(request.getDate());
    timeLog.setDescription(request.getDescription());
    timeLog.setWorkType(request.getWorkType());

    // Save and return the new time log
    return timeLogRepository.save(timeLog);
  }

  @Override
  public List<TimeLog> getLogsForService(String serviceId) {
    // Get all time logs associated with a specific service
    return timeLogRepository.findByServiceId(serviceId);
  }

  @Override
  public Optional<TimeLog> getLogDetails(String logId, String employeeId) {
    // Find the time log by ID and verify it belongs to the employee
    Optional<TimeLog> timeLog = timeLogRepository.findByIdAndEmployeeId(logId, employeeId);

    // If not found or doesn't belong to employee, throw access denied
    if (timeLog.isEmpty()) {
      throw new AccessDeniedException("Access denied: Time log not found or you don't have permission to access it");
    }

    return timeLog;
  }

  @Override
  public TimeLog updateLog(String logId, TimeLogUpdateRequest request, String employeeId) {
    // First verify the employee owns this log
    TimeLog timeLog = getLogDetails(logId, employeeId)
            .orElseThrow(() -> new AccessDeniedException("Cannot update: Time log not found"));

    // Update only the fields that are provided (not null)
    if (request.getServiceId() != null) {
      timeLog.setServiceId(request.getServiceId());
    }
    if (request.getProjectId() != null) {
      timeLog.setProjectId(request.getProjectId());
    }
    if (request.getHours() != null) {
      timeLog.setHours(request.getHours());
    }
    if (request.getDate() != null) {
      timeLog.setDate(request.getDate());
    }
    if (request.getDescription() != null) {
      timeLog.setDescription(request.getDescription());
    }
    if (request.getWorkType() != null) {
      timeLog.setWorkType(request.getWorkType());
    }

    // Save and return the updated log
    return timeLogRepository.save(timeLog);
  }

  @Override
  public void deleteLog(String logId, String employeeId) {
    // First verify the employee owns this log
    TimeLog timeLog = getLogDetails(logId, employeeId)
            .orElseThrow(() -> new AccessDeniedException("Cannot delete: Time log not found"));

    // Delete the time log
    timeLogRepository.delete(timeLog);
  }

  @Override
  public TimeLogSummaryResponse getEmployeeSummary(String employeeId, String period, String date) {
    LocalDate startDate;
    LocalDate endDate;

    // Calculate the date range based on the period type
    if ("month".equalsIgnoreCase(period)) {
      // For monthly summary: date should be in format "2024-10" (YYYY-MM)
      YearMonth yearMonth = YearMonth.parse(date);
      startDate = yearMonth.atDay(1);
      endDate = yearMonth.atEndOfMonth();
    } else if ("week".equalsIgnoreCase(period)) {
      // For weekly summary: date should be in format "2024-10-15" (start of week)
      startDate = LocalDate.parse(date);
      endDate = startDate.plusDays(6); // 7 days total
    } else {
      throw new IllegalArgumentException("Invalid period. Use 'month' or 'week'");
    }

    // Fetch all time logs for the employee in the date range
    List<TimeLog> logs = timeLogRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

    // Calculate total hours
    double totalHours = logs.stream()
            .mapToDouble(TimeLog::getHours)
            .sum();

    // Group hours by service
    Map<String, Double> byService = new HashMap<>();
    for (TimeLog log : logs) {
      String serviceId = log.getServiceId();
      if (serviceId != null) {
        byService.put(serviceId, byService.getOrDefault(serviceId, 0.0) + log.getHours());
      }
    }

    // Group hours by project
    Map<String, Double> byProject = new HashMap<>();
    for (TimeLog log : logs) {
      String projectId = log.getProjectId();
      if (projectId != null) {
        byProject.put(projectId, byProject.getOrDefault(projectId, 0.0) + log.getHours());
      }
    }

    // Build and return the summary response
    TimeLogSummaryResponse summary = new TimeLogSummaryResponse();
    summary.setEmployeeId(employeeId);
    summary.setPeriod(period);
    summary.setTotalHours(totalHours);
    summary.setCount(logs.size());
    summary.setByService(byService);
    summary.setByProject(byProject);

    return summary;
  }
}
package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.dto.mapper.TimeLogMapper;
import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.exception.ResourceNotFoundException;
import com.techtorque.time_logging_service.exception.UnauthorizedAccessException;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for Time Logging operations
 * 
 * Handles business logic including:
 * - CRUD operations for time logs
 * - Authorization checks (employees can only modify their own logs)
 * - Aggregations and summaries (daily, weekly, by service, by project)
 * - Statistics and analytics
 */
@Service
public class TimeLogService {

  private static final Logger logger = LoggerFactory.getLogger(TimeLogService.class);
  private final TimeLogRepository timeLogRepository;

  public TimeLogService(TimeLogRepository timeLogRepository) {
    this.timeLogRepository = timeLogRepository;
  }

  /**
   * Create a new time log entry
   * 
   * @param employeeId ID of the employee logging time
   * @param request Time log details
   * @return Created time log response
   */
  @Transactional
  public TimeLogResponse createTimeLog(String employeeId, TimeLogRequest request) {
    logger.info("Creating time log for employee: {}", employeeId);
    
    TimeLog timeLog = new TimeLog();
    timeLog.setEmployeeId(employeeId);
    timeLog.setServiceId(request.getServiceId());
    timeLog.setProjectId(request.getProjectId());
    timeLog.setHours(request.getHours());
    timeLog.setDate(request.getDate());
    timeLog.setDescription(request.getDescription());
    timeLog.setWorkType(request.getWorkType());
    
    TimeLog saved = timeLogRepository.save(timeLog);
    logger.info("Time log created successfully with ID: {}", saved.getId());
    
    return TimeLogMapper.toResponse(saved);
  }

  /**
   * Get a time log by ID (no authorization check)
   * 
   * @param id Time log ID
   * @return Time log response
   * @throws ResourceNotFoundException if not found
   */
  public TimeLogResponse getTimeLogById(String id) {
    TimeLog timeLog = timeLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + id));
    return TimeLogMapper.toResponse(timeLog);
  }

  /**
   * Get a time log by ID with authorization check
   * Employees can only view their own logs; admins can view all
   * 
   * @param logId Time log ID
   * @param userId User ID making the request
   * @param userRole User role (EMPLOYEE, ADMIN, etc.)
   * @return Time log response
   * @throws ResourceNotFoundException if not found
   * @throws UnauthorizedAccessException if not authorized
   */
  public TimeLogResponse getTimeLogByIdWithAuthorization(String logId, String userId, String userRole) {
    TimeLog timeLog = timeLogRepository.findById(logId)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + logId));
    
    // Admins can view all logs
    if (userRole != null && (userRole.contains("ADMIN") || userRole.contains("ROLE_ADMIN"))) {
      return TimeLogMapper.toResponse(timeLog);
    }
    
    // Employees can only view their own logs
    if (!timeLog.getEmployeeId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not authorized to view this time log");
    }
    
    return TimeLogMapper.toResponse(timeLog);
  }

  /**
   * Get all time logs for a specific employee
   * 
   * @param employeeId Employee ID
   * @return List of time log responses
   */
  public List<TimeLogResponse> getAllTimeLogsByEmployee(String employeeId) {
    logger.info("Fetching all time logs for employee: {}", employeeId);
    return timeLogRepository.findByEmployeeId(employeeId)
            .stream()
            .map(TimeLogMapper::toResponse)
            .collect(Collectors.toList());
  }

  /**
   * Get time logs for an employee within a date range
   * 
   * @param employeeId Employee ID
   * @param startDate Start date (inclusive)
   * @param endDate End date (inclusive)
   * @return List of time log responses
   */
  public List<TimeLogResponse> getTimeLogsByDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
    logger.info("Fetching time logs for employee: {} from {} to {}", employeeId, startDate, endDate);
    return timeLogRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate)
            .stream()
            .map(TimeLogMapper::toResponse)
            .collect(Collectors.toList());
  }

  /**
   * Get all time logs for a specific service
   * 
   * @param serviceId Service ID
   * @return List of time log responses
   */
  public List<TimeLogResponse> getTimeLogsByServiceId(String serviceId) {
    logger.info("Fetching time logs for service: {}", serviceId);
    return timeLogRepository.findByServiceId(serviceId)
            .stream()
            .map(TimeLogMapper::toResponse)
            .collect(Collectors.toList());
  }

  /**
   * Get all time logs for a specific project
   * 
   * @param projectId Project ID
   * @return List of time log responses
   */
  public List<TimeLogResponse> getTimeLogsByProjectId(String projectId) {
    logger.info("Fetching time logs for project: {}", projectId);
    return timeLogRepository.findByProjectId(projectId)
            .stream()
            .map(TimeLogMapper::toResponse)
            .collect(Collectors.toList());
  }

  /**
   * Update a time log entry (no authorization check)
   * 
   * @param id Time log ID
   * @param request Update request with new values
   * @return Updated time log response
   * @throws ResourceNotFoundException if not found
   */
  @Transactional
  public TimeLogResponse updateTimeLog(String id, TimeLogUpdateRequest request) {
    TimeLog timeLog = timeLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + id));

    TimeLogMapper.applyUpdate(request, timeLog);
    TimeLog updated = timeLogRepository.save(timeLog);
    
    return TimeLogMapper.toResponse(updated);
  }

  /**
   * Update a time log entry with authorization check
   * Employees can only update their own logs
   * 
   * @param logId Time log ID
   * @param employeeId Employee ID making the request
   * @param request Update request
   * @return Updated time log response
   * @throws ResourceNotFoundException if not found
   * @throws UnauthorizedAccessException if not authorized
   */
  @Transactional
  public TimeLogResponse updateTimeLogWithAuthorization(String logId, String employeeId, TimeLogUpdateRequest request) {
    TimeLog timeLog = timeLogRepository.findById(logId)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + logId));

    // Verify ownership
    if (!timeLog.getEmployeeId().equals(employeeId)) {
      throw new UnauthorizedAccessException("You are not authorized to update this time log");
    }

    TimeLogMapper.applyUpdate(request, timeLog);
    TimeLog updated = timeLogRepository.save(timeLog);
    
    logger.info("Time log {} updated by employee {}", logId, employeeId);
    return TimeLogMapper.toResponse(updated);
  }

  /**
   * Delete a time log entry (no authorization check)
   * 
   * @param id Time log ID
   * @throws ResourceNotFoundException if not found
   */
  @Transactional
  public void deleteTimeLog(String id) {
    if (!timeLogRepository.existsById(id)) {
      throw new ResourceNotFoundException("Time log not found with id: " + id);
    }
    timeLogRepository.deleteById(id);
  }

  /**
   * Delete a time log entry with authorization check
   * Employees can only delete their own logs
   * 
   * @param logId Time log ID
   * @param employeeId Employee ID making the request
   * @throws ResourceNotFoundException if not found
   * @throws UnauthorizedAccessException if not authorized
   */
  @Transactional
  public void deleteTimeLogWithAuthorization(String logId, String employeeId) {
    TimeLog timeLog = timeLogRepository.findById(logId)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + logId));

    // Verify ownership
    if (!timeLog.getEmployeeId().equals(employeeId)) {
      throw new UnauthorizedAccessException("You are not authorized to delete this time log");
    }

    timeLogRepository.deleteById(logId);
    logger.info("Time log {} deleted by employee {}", logId, employeeId);
  }

  /**
   * Get total hours logged by an employee
   * 
   * @param employeeId Employee ID
   * @return Total hours (0.0 if no logs)
   */
  public Double getTotalHoursByEmployee(String employeeId) {
    Double total = timeLogRepository.getTotalHoursByEmployeeId(employeeId);
    return total != null ? total : 0.0;
  }

  /**
   * Get employee summary for a specific date range
   * 
   * @param employeeId Employee ID
   * @param startDate Start date
   * @param endDate End date
   * @return Summary response with aggregated data
   */
  public TimeLogSummaryResponse getEmployeeSummary(String employeeId, LocalDate startDate, LocalDate endDate) {
    logger.info("Generating summary for employee {} from {} to {}", employeeId, startDate, endDate);
    
    List<TimeLog> logs = timeLogRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

    TimeLogSummaryResponse summary = new TimeLogSummaryResponse();
    summary.setEmployeeId(employeeId);
    summary.setPeriod(startDate + " to " + endDate);
    summary.setCount(logs.size());

    // Calculate total hours
    double totalHours = logs.stream()
            .mapToDouble(TimeLog::getHours)
            .sum();
    summary.setTotalHours(totalHours);

    // Group by service
    Map<String, Double> byService = new HashMap<>();
    logs.stream()
            .filter(log -> log.getServiceId() != null)
            .forEach(log -> byService.merge(log.getServiceId(), log.getHours(), Double::sum));
    summary.setByService(byService);

    // Group by project
    Map<String, Double> byProject = new HashMap<>();
    logs.stream()
            .filter(log -> log.getProjectId() != null)
            .forEach(log -> byProject.merge(log.getProjectId(), log.getHours(), Double::sum));
    summary.setByProject(byProject);

    return summary;
  }

  /**
   * Get employee summary by period (daily or weekly)
   * 
   * @param employeeId Employee ID
   * @param period Period type: "daily" or "weekly"
   * @param date Reference date
   * @return Summary response
   * @throws IllegalArgumentException if period is invalid
   */
  public TimeLogSummaryResponse getEmployeeSummaryByPeriod(String employeeId, String period, LocalDate date) {
    LocalDate startDate;
    LocalDate endDate;

    switch (period.toLowerCase()) {
      case "daily":
        // Summary for the specified day only
        startDate = date;
        endDate = date;
        break;
      case "weekly":
        // Summary for the week containing the specified date (Monday to Sunday)
        startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        break;
      default:
        throw new IllegalArgumentException("Invalid period. Must be 'daily' or 'weekly'");
    }

    logger.info("Generating {} summary for employee {} (reference date: {})", period, employeeId, date);
    return getEmployeeSummary(employeeId, startDate, endDate);
  }

  /**
   * Get employee statistics
   * Provides a quick overview including total hours, log count, and breakdowns
   * 
   * @param employeeId Employee ID
   * @return Map containing various statistics
   */
  public Map<String, Object> getEmployeeStatistics(String employeeId) {
    logger.info("Generating statistics for employee: {}", employeeId);
    
    List<TimeLog> allLogs = timeLogRepository.findByEmployeeId(employeeId);
    
    Map<String, Object> stats = new HashMap<>();
    stats.put("employeeId", employeeId);
    stats.put("totalLogs", allLogs.size());
    
    // Total hours
    double totalHours = allLogs.stream()
            .mapToDouble(TimeLog::getHours)
            .sum();
    stats.put("totalHours", totalHours);
    
    // Average hours per log
    double avgHours = allLogs.isEmpty() ? 0.0 : totalHours / allLogs.size();
    stats.put("averageHoursPerLog", Math.round(avgHours * 100.0) / 100.0);
    
    // Count by work type
    Map<String, Long> byWorkType = allLogs.stream()
            .filter(log -> log.getWorkType() != null)
            .collect(Collectors.groupingBy(TimeLog::getWorkType, Collectors.counting()));
    stats.put("logsByWorkType", byWorkType);
    
    // Hours by service
    Map<String, Double> byService = new HashMap<>();
    allLogs.stream()
            .filter(log -> log.getServiceId() != null)
            .forEach(log -> byService.merge(log.getServiceId(), log.getHours(), Double::sum));
    stats.put("hoursByService", byService);
    
    // Hours by project
    Map<String, Double> byProject = new HashMap<>();
    allLogs.stream()
            .filter(log -> log.getProjectId() != null)
            .forEach(log -> byProject.merge(log.getProjectId(), log.getHours(), Double::sum));
    stats.put("hoursByProject", byProject);
    
    // Date range
    if (!allLogs.isEmpty()) {
      LocalDate firstDate = allLogs.stream()
              .map(TimeLog::getDate)
              .min(LocalDate::compareTo)
              .orElse(null);
      LocalDate lastDate = allLogs.stream()
              .map(TimeLog::getDate)
              .max(LocalDate::compareTo)
              .orElse(null);
      stats.put("firstLogDate", firstDate);
      stats.put("lastLogDate", lastDate);
    }
    
    return stats;
  }
}


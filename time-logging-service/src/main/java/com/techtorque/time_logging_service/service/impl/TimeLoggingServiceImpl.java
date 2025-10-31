package com.techtorque.time_logging_service.service.impl;

import com.techtorque.time_logging_service.dto.TimeLogRequestDto;
import com.techtorque.time_logging_service.dto.TimeLogUpdateDto;
import com.techtorque.time_logging_service.dto.TimeSummaryDto;
import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.exception.TimeLogNotFoundException;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import com.techtorque.time_logging_service.service.TimeLoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class TimeLoggingServiceImpl implements TimeLoggingService {

  private final TimeLogRepository timeLogRepository;

  public TimeLoggingServiceImpl(TimeLogRepository timeLogRepository) {
    this.timeLogRepository = timeLogRepository;
  }

  @Override
  public TimeLog logWorkTime(TimeLogRequestDto dto, String employeeId) {
    log.info("Logging work time for employee: {} on service: {}", employeeId, dto.getServiceId());

    TimeLog newLog = TimeLog.builder()
            .employeeId(employeeId)
            .serviceId(dto.getServiceId())
            .hours(dto.getHours())
            .date(dto.getDate())
            .description(dto.getDescription())
            .workType(dto.getWorkType())
            .build();

    TimeLog savedLog = timeLogRepository.save(newLog);
    log.info("Successfully logged {} hours for employee: {}", dto.getHours(), employeeId);

    return savedLog;
  }

  @Override
  public List<TimeLog> getLogsForService(String serviceId) {
    log.info("Fetching all time logs for service: {}", serviceId);
    return timeLogRepository.findByServiceId(serviceId);
  }

  @Override
  public Optional<TimeLog> getLogDetails(String logId, String employeeId) {
    log.info("Fetching time log {} for employee: {}", logId, employeeId);

    // Find log and verify ownership
    Optional<TimeLog> logOpt = timeLogRepository.findByIdAndEmployeeId(logId, employeeId);

    if (logOpt.isEmpty()) {
      log.warn("Time log {} not found or access denied for employee: {}", logId, employeeId);
    }

    return logOpt;
  }

  @Override
  public TimeLog updateLog(String logId, TimeLogUpdateDto dto, String employeeId) {
    log.info("Updating time log {} for employee: {}", logId, employeeId);

    // Find log and verify ownership
    TimeLog existingLog = timeLogRepository.findByIdAndEmployeeId(logId, employeeId)
            .orElseThrow(() -> {
              log.warn("Time log {} not found for employee: {}", logId, employeeId);
              return new TimeLogNotFoundException("Time log not found or you don't have permission to update it");
            });

    // Update fields if provided
    if (dto.getHours() != null) {
      existingLog.setHours(dto.getHours());
    }
    if (dto.getDescription() != null) {
      existingLog.setDescription(dto.getDescription());
    }
    if (dto.getWorkType() != null) {
      existingLog.setWorkType(dto.getWorkType());
    }

    TimeLog updatedLog = timeLogRepository.save(existingLog);
    log.info("Successfully updated time log: {}", logId);

    return updatedLog;
  }

  @Override
  public void deleteLog(String logId, String employeeId) {
    log.info("Deleting time log {} for employee: {}", logId, employeeId);

    // Find log and verify ownership
    TimeLog existingLog = timeLogRepository.findByIdAndEmployeeId(logId, employeeId)
            .orElseThrow(() -> {
              log.warn("Time log {} not found for employee: {}", logId, employeeId);
              return new TimeLogNotFoundException("Time log not found or you don't have permission to delete it");
            });

    timeLogRepository.delete(existingLog);
    log.info("Successfully deleted time log: {}", logId);
  }

  @Override
  public TimeSummaryDto getEmployeeSummary(String employeeId, String period, LocalDate date) {
    log.info("Generating {} summary for employee: {} on date: {}", period, employeeId, date);

    LocalDate startDate;
    LocalDate endDate;

    // Calculate date range based on period
    if ("weekly".equalsIgnoreCase(period)) {
      // Get start of week (Monday)
      startDate = date.with(DayOfWeek.MONDAY);
      endDate = date.with(DayOfWeek.SUNDAY);
    } else { // daily
      startDate = date;
      endDate = date;
    }

    // Fetch logs for the period
    List<TimeLog> logs = timeLogRepository.findByEmployeeIdAndDateBetween(
            employeeId, startDate, endDate);

    // Calculate total hours
    double totalHours = logs.stream()
            .mapToDouble(TimeLog::getHours)
            .sum();

    TimeSummaryDto summary = TimeSummaryDto.builder()
            .employeeId(employeeId)
            .startDate(startDate)
            .endDate(endDate)
            .totalHours(totalHours)
            .totalEntries(logs.size())
            .period(period)
            .build();

    log.info("Summary generated: {} hours logged across {} entries", totalHours, logs.size());

    return summary;
  }

  @Override
  public List<TimeLog> getMyLogs(String employeeId, LocalDate fromDate, LocalDate toDate) {
    log.info("Fetching logs for employee: {} from {} to {}", employeeId, fromDate, toDate);

    if (fromDate == null || toDate == null) {
      // Default to all logs if no date range specified
      return timeLogRepository.findByEmployeeIdOrderByDateDesc(employeeId);
    }

    return timeLogRepository.findByEmployeeIdAndDateBetween(employeeId, fromDate, toDate);
  }
}
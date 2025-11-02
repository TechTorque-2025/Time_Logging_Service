package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.dto.mapper.TimeLogMapper;
import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.exception.ResourceNotFoundException;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimeLogService {

  private final TimeLogRepository timeLogRepository;

  public TimeLogService(TimeLogRepository timeLogRepository) {
    this.timeLogRepository = timeLogRepository;
  }

  @Transactional
  public TimeLogResponse createTimeLog(String employeeId, TimeLogRequest request) {
    TimeLog timeLog = new TimeLog();
    timeLog.setEmployeeId(employeeId);
    timeLog.setServiceId(request.getServiceId());
    timeLog.setProjectId(request.getProjectId());
    timeLog.setHours(request.getHours());
    timeLog.setDate(request.getDate());
    timeLog.setDescription(request.getDescription());
    timeLog.setWorkType(request.getWorkType());
    TimeLog saved = timeLogRepository.save(timeLog);
    return TimeLogMapper.toResponse(saved);
  }

  public TimeLogResponse getTimeLogById(String id) {
    TimeLog timeLog = timeLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + id));
    return TimeLogMapper.toResponse(timeLog);
  }

  public List<TimeLogResponse> getAllTimeLogsByEmployee(String employeeId) {
    return timeLogRepository.findByEmployeeId(employeeId)
            .stream()
            .map(TimeLogMapper::toResponse)
            .collect(Collectors.toList());
  }

  public List<TimeLogResponse> getTimeLogsByDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
    return timeLogRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate)
            .stream()
            .map(TimeLogMapper::toResponse)
            .collect(Collectors.toList());
  }

  @Transactional
  public TimeLogResponse updateTimeLog(String id, TimeLogUpdateRequest request) {
    TimeLog timeLog = timeLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Time log not found with id: " + id));

    TimeLogMapper.applyUpdate(request, timeLog);
    TimeLog updated = timeLogRepository.save(timeLog);
    return TimeLogMapper.toResponse(updated);
  }

  @Transactional
  public void deleteTimeLog(String id) {
    if (!timeLogRepository.existsById(id)) {
      throw new ResourceNotFoundException("Time log not found with id: " + id);
    }
    timeLogRepository.deleteById(id);
  }

  public Double getTotalHoursByEmployee(String employeeId) {
    Double total = timeLogRepository.getTotalHoursByEmployeeId(employeeId);
    return total != null ? total : 0.0;
  }

  public TimeLogSummaryResponse getEmployeeSummary(String employeeId, LocalDate startDate, LocalDate endDate) {
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
}


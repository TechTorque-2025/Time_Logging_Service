package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.dto.TimeLogRequestDto;
import com.techtorque.time_logging_service.dto.TimeLogUpdateDto;
import com.techtorque.time_logging_service.dto.TimeSummaryDto;
import com.techtorque.time_logging_service.entity.TimeLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeLoggingService {

  TimeLog logWorkTime(TimeLogRequestDto dto, String employeeId);

  List<TimeLog> getLogsForService(String serviceId);

  Optional<TimeLog> getLogDetails(String logId, String employeeId);

  TimeLog updateLog(String logId, TimeLogUpdateDto dto, String employeeId);

  void deleteLog(String logId, String employeeId);

  TimeSummaryDto getEmployeeSummary(String employeeId, String period, LocalDate date);

  List<TimeLog> getMyLogs(String employeeId, LocalDate fromDate, LocalDate toDate);
}
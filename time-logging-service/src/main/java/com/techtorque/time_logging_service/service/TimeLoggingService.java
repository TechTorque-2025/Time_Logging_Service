package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.entity.TimeLog;
import java.util.List;
import java.util.Optional;

public interface TimeLoggingService {

  TimeLog logWorkTime(/* TimeLogRequestDto dto, */ String employeeId);

  List<TimeLog> getLogsForService(String serviceId);

  Optional<TimeLog> getLogDetails(String logId, String employeeId);

  TimeLog updateLog(String logId, /* TimeLogUpdateDto dto, */ String employeeId);

  void deleteLog(String logId, String employeeId);

  Object getEmployeeSummary(String employeeId, String period, String date); // Return type would be a Summary DTO
}
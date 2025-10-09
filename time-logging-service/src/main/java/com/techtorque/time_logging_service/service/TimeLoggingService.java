package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.entity.TimeLog;
import java.util.List;

public interface TimeLoggingService {

  TimeLog logWorkTime(/* TimeLogRequestDto dto, */ String employeeId);

  List<TimeLog> getLogsForService(String serviceId);

  // Other method signatures for update, delete, get by employee etc.
}
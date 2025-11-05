package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.entity.TimeLog;

import java.util.List;
import java.util.Optional;

public interface TimeLoggingService {
    TimeLog logWorkTime(TimeLogRequest request, String employeeId);
    List<TimeLog> getLogsForService(String serviceId);
    Optional<TimeLog> getLogDetails(String logId, String employeeId);
    TimeLog updateLog(String logId, TimeLogUpdateRequest request, String employeeId);
    void deleteLog(String logId, String employeeId);
    TimeLogSummaryResponse getEmployeeSummary(String employeeId, String period, String date);
}
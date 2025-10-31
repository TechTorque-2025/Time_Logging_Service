package com.techtorque.time_logging_service.controller;

import com.techtorque.time_logging_service.dto.*;
import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.service.TimeLoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/time-logs")
@Tag(name = "Time Logging", description = "Endpoints for employees to log work time.")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TimeLoggingController {

  private final TimeLoggingService timeLoggingService;

  @Operation(summary = "Log work time for a service or project (employee only)")
  @PostMapping
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<ApiResponse> logWorkTime(
          @Valid @RequestBody TimeLogRequestDto dto,
          @RequestHeader("X-User-Subject") String employeeId) {

    TimeLog timeLog = timeLoggingService.logWorkTime(dto, employeeId);
    TimeLogResponseDto response = mapToResponseDto(timeLog);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Time logged successfully", response));
  }

  @Operation(summary = "Get an employee's time logs for a given period")
  @GetMapping
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<ApiResponse> getMyLogs(
          @RequestHeader("X-User-Subject") String employeeId,
          @RequestParam(required = false) LocalDate fromDate,
          @RequestParam(required = false) LocalDate toDate) {

    List<TimeLog> timeLogs = timeLoggingService.getMyLogs(employeeId, fromDate, toDate);
    List<TimeLogResponseDto> response = timeLogs.stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(ApiResponse.success("Time logs retrieved successfully", response));
  }

  @Operation(summary = "Get details for a specific time log entry")
  @GetMapping("/{logId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<ApiResponse> getLogDetails(
          @PathVariable String logId,
          @RequestHeader("X-User-Subject") String employeeId) {

    TimeLog timeLog = timeLoggingService.getLogDetails(logId, employeeId)
            .orElseThrow(() -> new RuntimeException("Time log not found or access denied"));

    TimeLogResponseDto response = mapToResponseDto(timeLog);
    return ResponseEntity.ok(ApiResponse.success("Time log retrieved successfully", response));
  }

  @Operation(summary = "Update a time log entry (employee can only update their own)")
  @PutMapping("/{logId}")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<ApiResponse> updateLog(
          @PathVariable String logId,
          @Valid @RequestBody TimeLogUpdateDto dto,
          @RequestHeader("X-User-Subject") String employeeId) {

    TimeLog timeLog = timeLoggingService.updateLog(logId, dto, employeeId);
    TimeLogResponseDto response = mapToResponseDto(timeLog);

    return ResponseEntity.ok(ApiResponse.success("Time log updated successfully", response));
  }

  @Operation(summary = "Delete a time log entry (employee can only delete their own)")
  @DeleteMapping("/{logId}")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<ApiResponse> deleteLog(
          @PathVariable String logId,
          @RequestHeader("X-User-Subject") String employeeId) {

    timeLoggingService.deleteLog(logId, employeeId);
    return ResponseEntity.ok(ApiResponse.success("Time log deleted successfully"));
  }

  @Operation(summary = "Get a daily or weekly summary of work logged (employee only)")
  @GetMapping("/summary")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<ApiResponse> getSummary(
          @RequestHeader("X-User-Subject") String employeeId,
          @RequestParam String period,
          @RequestParam LocalDate date) {

    TimeSummaryDto summary = timeLoggingService.getEmployeeSummary(employeeId, period, date);
    return ResponseEntity.ok(ApiResponse.success("Summary generated successfully", summary));
  }

  @Operation(summary = "Get all time logs for a specific service (for internal/customer/employee use)")
  @GetMapping("/service/{serviceId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'CUSTOMER')")
  public ResponseEntity<ApiResponse> getLogsForService(@PathVariable String serviceId) {
    List<TimeLog> timeLogs = timeLoggingService.getLogsForService(serviceId);
    List<TimeLogResponseDto> response = timeLogs.stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(ApiResponse.success("Service logs retrieved successfully", response));
  }

  // Helper method to map Entity to DTO
  private TimeLogResponseDto mapToResponseDto(TimeLog timeLog) {
    return TimeLogResponseDto.builder()
            .id(timeLog.getId())
            .employeeId(timeLog.getEmployeeId())
            .serviceId(timeLog.getServiceId())
            .hours(timeLog.getHours())
            .date(timeLog.getDate())
            .description(timeLog.getDescription())
            .workType(timeLog.getWorkType())
            .createdAt(timeLog.getCreatedAt())
            .updatedAt(timeLog.getUpdatedAt())
            .build();
  }
}
package com.techtorque.time_logging_service.controller;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.exception.UnauthorizedAccessException;
import com.techtorque.time_logging_service.service.TimeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Time Logging Service
 * 
 * Handles all time log operations per the TechTorque API Design:
 * - Create, read, update, delete time logs (employees only)
 * - Query logs by employee, date range, service, or project
 * - Generate daily/weekly summaries
 * - Get service-specific time logs (accessible to customers and employees)
 * 
 * All endpoints require authentication except where noted.
 * Role-based access control is enforced via @PreAuthorize annotations.
 */
@RestController
@RequestMapping("/time-logs")
@Tag(name = "Time Logging", description = "Employee time tracking and work hour logging endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TimeLogController {

  private final TimeLogService timeLogService;

  public TimeLogController(TimeLogService timeLogService) {
    this.timeLogService = timeLogService;
  }

  /**
   * POST /time-logs - Log work time
   * Allows employees to create a new time log entry for work performed
   * Requires EMPLOYEE role
   */
  @Operation(
    summary = "Log work time",
    description = "Create a new time log entry for a service or project. Employee ID is extracted from authentication headers."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Time log created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid authentication"),
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
  })
  @PostMapping
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<TimeLogResponse> createTimeLog(
          @Parameter(description = "Employee ID from authentication token", required = true)
          @RequestHeader(value = "X-User-Subject") String employeeId,
          @Valid @RequestBody TimeLogRequest request) {

    TimeLogResponse response = timeLogService.createTimeLog(employeeId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * GET /time-logs - Get employee's time logs
   * Returns all time logs for the authenticated employee
   * Optional query parameters for filtering: from, to
   */
  @Operation(
    summary = "Get employee's time logs",
    description = "Retrieve all time log entries for the authenticated employee. Optionally filter by date range."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved time logs"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  @GetMapping
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<TimeLogResponse>> getMyTimeLogs(
          @Parameter(description = "Employee ID from authentication token", required = true)
          @RequestHeader("X-User-Subject") String userId,
          @RequestHeader("X-User-Roles") String roles,
          @Parameter(description = "Start date for filtering (YYYY-MM-DD)")
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
          @Parameter(description = "End date for filtering (YYYY-MM-DD)")
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    List<TimeLogResponse> responses;

    // Admin and Super Admin can see all time logs
    if (roles.contains("ADMIN") || roles.contains("SUPER_ADMIN")) {
      if (from != null && to != null) {
        // For admin with date range, get all logs and filter (or create new method)
        responses = timeLogService.getAllTimeLogs().stream()
            .filter(log -> !log.getDate().isBefore(from) && !log.getDate().isAfter(to))
            .collect(java.util.stream.Collectors.toList());
      } else {
        responses = timeLogService.getAllTimeLogs();
      }
    } else {
      // Employee sees only their own logs
      if (from != null && to != null) {
        responses = timeLogService.getTimeLogsByDateRange(userId, from, to);
      } else {
        responses = timeLogService.getAllTimeLogsByEmployee(userId);
      }
    }

    return ResponseEntity.ok(responses);
  }

  /**
   * GET /time-logs/{logId} - Get log details
   * Retrieves a specific time log entry
   * Accessible to EMPLOYEE (own logs) and ADMIN (all logs)
   */
  @Operation(
    summary = "Get time log details",
    description = "Retrieve details of a specific time log entry. Employees can only access their own logs; admins can access any log."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved time log"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden - not authorized to view this log"),
    @ApiResponse(responseCode = "404", description = "Time log not found")
  })
  @GetMapping("/{logId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<TimeLogResponse> getTimeLogById(
          @Parameter(description = "Time log ID", required = true)
          @PathVariable String logId,
          @Parameter(description = "User ID from authentication token")
          @RequestHeader(value = "X-User-Subject", required = false) String userId,
          @Parameter(description = "User role from authentication token")
          @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    TimeLogResponse response = timeLogService.getTimeLogByIdWithAuthorization(logId, userId, userRole);
    return ResponseEntity.ok(response);
  }

  /**
   * PUT /time-logs/{logId} - Update log entry
   * Allows employees to update their own time log entries
   */
  @Operation(
    summary = "Update time log entry",
    description = "Update an existing time log entry. Employees can only update their own logs."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Time log updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden - not authorized to update this log"),
    @ApiResponse(responseCode = "404", description = "Time log not found")
  })
  @PutMapping("/{logId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<TimeLogResponse> updateTimeLog(
          @Parameter(description = "Time log ID", required = true)
          @PathVariable String logId,
          @Parameter(description = "Employee ID from authentication token", required = true)
          @RequestHeader("X-User-Subject") String employeeId,
          @Valid @RequestBody TimeLogUpdateRequest request) {

    TimeLogResponse response = timeLogService.updateTimeLogWithAuthorization(logId, employeeId, request);
    return ResponseEntity.ok(response);
  }

  /**
   * DELETE /time-logs/{logId} - Delete log entry
   * Allows employees to delete their own time log entries
   */
  @Operation(
    summary = "Delete time log entry",
    description = "Delete a time log entry. Employees can only delete their own logs."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Time log deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden - not authorized to delete this log"),
    @ApiResponse(responseCode = "404", description = "Time log not found")
  })
  @DeleteMapping("/{logId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Void> deleteTimeLog(
          @Parameter(description = "Time log ID", required = true)
          @PathVariable String logId,
          @Parameter(description = "Employee ID from authentication token", required = false)
          @RequestHeader(value = "X-User-Subject", required = false) String employeeId,
          @Parameter(description = "User role from authentication token", required = false)
          @RequestHeader(value = "X-User-Roles", required = false) String userRoles) {

    // If header is missing, check if user is admin (can delete any log)
    if (employeeId == null || employeeId.isEmpty()) {
      throw new UnauthorizedAccessException("User identification missing. Please ensure you are logged in.");
    }

    timeLogService.deleteTimeLogWithAuthorization(logId, employeeId);
    return ResponseEntity.noContent().build();
  }

  /**
   * GET /services/{serviceId}/time-logs - Get service time logs
   * Retrieves all time logs associated with a specific service
   * Accessible to CUSTOMER (own services), EMPLOYEE, and ADMIN
   */
  @Operation(
    summary = "Get time logs for a service",
    description = "Retrieve all time log entries associated with a specific service. Useful for tracking work progress and hours spent."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved time logs"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  @GetMapping("/service/{serviceId}")
  @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
  public ResponseEntity<List<TimeLogResponse>> getTimeLogsForService(
          @Parameter(description = "Service ID", required = true)
          @PathVariable String serviceId) {
    
    List<TimeLogResponse> responses = timeLogService.getTimeLogsByServiceId(serviceId);
    return ResponseEntity.ok(responses);
  }

  /**
   * GET /time-logs/summary - Daily/weekly summary
   * Provides aggregated time log data for the authenticated employee
   * Query parameters: period (daily|weekly), date (YYYY-MM-DD)
   */
  @Operation(
    summary = "Get time log summary",
    description = "Retrieve a summary of time logs for the authenticated employee. " +
                  "Specify 'daily' or 'weekly' period and a reference date."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved summary"),
    @ApiResponse(responseCode = "400", description = "Invalid period or date parameter"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden")
  })
  @GetMapping("/summary")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<TimeLogSummaryResponse> getSummary(
          @Parameter(description = "Employee ID from authentication token", required = true)
          @RequestHeader("X-User-Subject") String employeeId,
          @Parameter(description = "Period type: 'daily' or 'weekly'", required = true)
          @RequestParam String period,
          @Parameter(description = "Reference date (YYYY-MM-DD)", required = true)
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    TimeLogSummaryResponse summary = timeLogService.getEmployeeSummaryByPeriod(employeeId, period, date);
    return ResponseEntity.ok(summary);
  }

  // Additional convenience endpoints (not in original design but useful)

  /**
   * GET /time-logs/project/{projectId} - Get project time logs
   * Retrieves all time logs associated with a specific project
   */
  @Operation(
    summary = "Get time logs for a project",
    description = "Retrieve all time log entries associated with a specific project."
  )
  @GetMapping("/project/{projectId}")
  @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
  public ResponseEntity<List<TimeLogResponse>> getTimeLogsForProject(
          @Parameter(description = "Project ID", required = true)
          @PathVariable String projectId) {
    
    List<TimeLogResponse> responses = timeLogService.getTimeLogsByProjectId(projectId);
    return ResponseEntity.ok(responses);
  }

  /**
   * GET /time-logs/stats - Get statistics
   * Provides quick statistics for the authenticated employee
   */
  @Operation(
    summary = "Get employee statistics",
    description = "Get quick statistics including total hours logged, number of logs, and breakdown by service/project."
  )
  @GetMapping("/stats")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Map<String, Object>> getEmployeeStats(
          @Parameter(description = "Employee ID from authentication token", required = true)
          @RequestHeader("X-User-Subject") String employeeId) {

    Map<String, Object> stats = timeLogService.getEmployeeStatistics(employeeId);
    return ResponseEntity.ok(stats);
  }
}

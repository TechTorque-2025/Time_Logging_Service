package com.techtorque.time_logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/time-logs")
@Tag(name = "Time Logging", description = "Endpoints for employees to log work time.")
@SecurityRequirement(name = "bearerAuth")
public class TimeLoggingController {

  // @Autowired
  // private TimeLoggingService timeLoggingService;

  @Operation(summary = "Log work time for a service or project (employee only)")
  @PostMapping
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<?> logWorkTime(
          // @RequestBody TimeLogRequestDto dto,
          @RequestHeader("X-User-Subject") String employeeId) {
    // TODO: Delegate to timeLoggingService.logWorkTime(dto, employeeId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get an employee's time logs for a given period")
  @GetMapping
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<List<?>> getMyLogs(
          @RequestHeader("X-User-Subject") String employeeId
          /* @RequestParam String fromDate, @RequestParam String toDate */) {
    // TODO: Delegate to a service method that calls repository.findByEmployeeIdAndDateBetween()
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get details for a specific time log entry")
  @GetMapping("/{logId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
  public ResponseEntity<?> getLogDetails(
          @PathVariable String logId,
          @RequestHeader("X-User-Subject") String employeeId) {
    // TODO: Delegate to timeLoggingService.getLogDetails(logId, employeeId);
    // The service layer must verify the employee owns this log or is an admin.
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Update a time log entry (employee can only update their own)")
  @PutMapping("/{logId}")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<?> updateLog(
          @PathVariable String logId,
          // @RequestBody TimeLogUpdateDto dto,
          @RequestHeader("X-User-Subject") String employeeId) {
    // TODO: Delegate to timeLoggingService.updateLog(logId, dto, employeeId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Delete a time log entry (employee can only delete their own)")
  @DeleteMapping("/{logId}")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<?> deleteLog(
          @PathVariable String logId,
          @RequestHeader("X-User-Subject") String employeeId) {
    // TODO: Delegate to timeLoggingService.deleteLog(logId, employeeId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get a daily or weekly summary of work logged (employee only)")
  @GetMapping("/summary")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<?> getSummary(
          @RequestHeader("X-User-Subject") String employeeId,
          @RequestParam String period, // "daily" or "weekly"
          @RequestParam String date) { // "YYYY-MM-DD"
    // TODO: Delegate to timeLoggingService.getEmployeeSummary(employeeId, period, date);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get all time logs for a specific service (for internal/customer/employee use)")
  @GetMapping("/service/{serviceId}")
  @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'CUSTOMER')")
  public ResponseEntity<List<?>> getLogsForService(@PathVariable String serviceId) {
    // TODO: Delegate to timeLoggingService.getLogsForService(serviceId);
    return ResponseEntity.ok().build();
  }
}
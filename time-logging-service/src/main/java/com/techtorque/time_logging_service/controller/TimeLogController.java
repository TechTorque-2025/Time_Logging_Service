package com.techtorque.time_logging_service.controller;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.service.TimeLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/time-logs")
public class TimeLogController {

  private final TimeLogService timeLogService;

  public TimeLogController(TimeLogService timeLogService) {
    this.timeLogService = timeLogService;
  }

  @PostMapping
  public ResponseEntity<TimeLogResponse> createTimeLog(
          @RequestHeader("X-Employee-Id") String employeeId,
          @Valid @RequestBody TimeLogRequest request) {
    TimeLogResponse response = timeLogService.createTimeLog(employeeId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TimeLogResponse> getTimeLogById(@PathVariable String id) {
    TimeLogResponse response = timeLogService.getTimeLogById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<List<TimeLogResponse>> getTimeLogsByEmployee(@PathVariable String employeeId) {
    List<TimeLogResponse> responses = timeLogService.getAllTimeLogsByEmployee(employeeId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/employee/{employeeId}/date-range")
  public ResponseEntity<List<TimeLogResponse>> getTimeLogsByDateRange(
          @PathVariable String employeeId,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    List<TimeLogResponse> responses = timeLogService.getTimeLogsByDateRange(employeeId, startDate, endDate);
    return ResponseEntity.ok(responses);
  }



  @PutMapping("/{id}")
  public ResponseEntity<TimeLogResponse> updateTimeLog(
          @PathVariable String id,
          @Valid @RequestBody TimeLogUpdateRequest request) {
    TimeLogResponse response = timeLogService.updateTimeLog(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTimeLog(@PathVariable String id) {
    timeLogService.deleteTimeLog(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/employee/{employeeId}/total-hours")
  public ResponseEntity<Double> getTotalHours(@PathVariable String employeeId) {
    Double totalHours = timeLogService.getTotalHoursByEmployee(employeeId);
    return ResponseEntity.ok(totalHours);
  }

  @GetMapping("/employee/{employeeId}/summary")
  public ResponseEntity<TimeLogSummaryResponse> getEmployeeSummary(
          @PathVariable String employeeId,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    TimeLogSummaryResponse summary = timeLogService.getEmployeeSummary(employeeId, startDate, endDate);
    return ResponseEntity.ok(summary);
  }
}

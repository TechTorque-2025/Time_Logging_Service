package com.techtorque.time_logging_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogResponseDto {
    private String id;
    private String employeeId;
    private String serviceId;
    private double hours;
    private LocalDate date;
    private String description;
    private String workType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

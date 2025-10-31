package com.techtorque.time_logging_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSummaryDto {
    private String employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalHours;
    private int totalEntries;
    private String period;
}

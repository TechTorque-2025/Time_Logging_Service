package com.techtorque.time_logging_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogRequestDto {

    @NotBlank(message = "Service ID is required")
    private String serviceId;

    @NotNull(message = "Hours worked is required")
    @Min(value = 0, message = "Hours must be positive")
    @Max(value = 24, message = "Hours cannot exceed 24 in a day")
    private Double hours;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Cannot log time for future dates")
    private LocalDate date;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Work type cannot exceed 100 characters")
    private String workType;
}

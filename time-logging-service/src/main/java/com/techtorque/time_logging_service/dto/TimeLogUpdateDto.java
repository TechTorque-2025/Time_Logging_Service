package com.techtorque.time_logging_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogUpdateDto {

    @Min(value = 0, message = "Hours must be positive")
    @Max(value = 24, message = "Hours cannot exceed 24 in a day")
    private Double hours;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Work type cannot exceed 100 characters")
    private String workType;
}

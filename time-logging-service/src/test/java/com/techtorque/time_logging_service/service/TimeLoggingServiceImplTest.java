package com.techtorque.time_logging_service.service;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogSummaryResponse;
import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import com.techtorque.time_logging_service.service.impl.TimeLoggingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeLoggingServiceImplTest {

    @Mock
    private TimeLogRepository timeLogRepository;

    @InjectMocks
    private TimeLoggingServiceImpl timeLoggingService;

    private TimeLog testTimeLog;
    private TimeLogRequest testRequest;

    @BeforeEach
    void setUp() {
        testTimeLog = TimeLog.builder()
                .id("log123")
                .employeeId("employee123")
                .serviceId("service456")
                .projectId("project789")
                .hours(8.0)
                .date(LocalDate.of(2025, 11, 21))
                .description("Worked on feature")
                .workType("Development")
                .build();

        testRequest = new TimeLogRequest();
        testRequest.setServiceId("service456");
        testRequest.setProjectId("project789");
        testRequest.setHours(8.0);
        testRequest.setDate(LocalDate.of(2025, 11, 21));
        testRequest.setDescription("Worked on feature");
        testRequest.setWorkType("Development");
    }

    @Test
    void testLogWorkTime_Success() {
        when(timeLogRepository.save(any(TimeLog.class))).thenReturn(testTimeLog);

        TimeLog result = timeLoggingService.logWorkTime(testRequest, "employee123");

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo("employee123");
        assertThat(result.getHours()).isEqualTo(8.0);
        verify(timeLogRepository, times(1)).save(any(TimeLog.class));
    }

    @Test
    void testGetLogsForService() {
        TimeLog log2 = TimeLog.builder()
                .id("log456")
                .employeeId("employee999")
                .serviceId("service456")
                .hours(4.0)
                .date(LocalDate.of(2025, 11, 22))
                .description("Another task")
                .build();

        when(timeLogRepository.findByServiceId("service456"))
                .thenReturn(Arrays.asList(testTimeLog, log2));

        List<TimeLog> results = timeLoggingService.getLogsForService("service456");

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(log -> log.getServiceId().equals("service456"));
        verify(timeLogRepository, times(1)).findByServiceId("service456");
    }

    @Test
    void testGetLogDetails_Success() {
        when(timeLogRepository.findByIdAndEmployeeId("log123", "employee123"))
                .thenReturn(Optional.of(testTimeLog));

        Optional<TimeLog> result = timeLoggingService.getLogDetails("log123", "employee123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("log123");
    }

    @Test
    void testGetLogDetails_AccessDenied() {
        when(timeLogRepository.findByIdAndEmployeeId("log123", "wrongEmployee"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeLoggingService.getLogDetails("log123", "wrongEmployee"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void testUpdateLog_Success() {
        TimeLogUpdateRequest updateRequest = new TimeLogUpdateRequest();
        updateRequest.setHours(10.0);
        updateRequest.setDescription("Updated description");

        when(timeLogRepository.findByIdAndEmployeeId("log123", "employee123"))
                .thenReturn(Optional.of(testTimeLog));
        when(timeLogRepository.save(any(TimeLog.class))).thenReturn(testTimeLog);

        TimeLog result = timeLoggingService.updateLog("log123", updateRequest, "employee123");

        assertThat(result).isNotNull();
        verify(timeLogRepository, times(1)).save(any(TimeLog.class));
    }

    @Test
    void testUpdateLog_AccessDenied() {
        TimeLogUpdateRequest updateRequest = new TimeLogUpdateRequest();
        updateRequest.setHours(10.0);

        when(timeLogRepository.findByIdAndEmployeeId("log123", "wrongEmployee"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeLoggingService.updateLog("log123", updateRequest, "wrongEmployee"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testUpdateLog_PartialUpdate() {
        TimeLogUpdateRequest updateRequest = new TimeLogUpdateRequest();
        updateRequest.setHours(10.0);
        // Other fields are null

        when(timeLogRepository.findByIdAndEmployeeId("log123", "employee123"))
                .thenReturn(Optional.of(testTimeLog));
        when(timeLogRepository.save(any(TimeLog.class))).thenAnswer(invocation -> {
            TimeLog saved = invocation.getArgument(0);
            assertThat(saved.getHours()).isEqualTo(10.0);
            assertThat(saved.getDescription()).isEqualTo("Worked on feature"); // unchanged
            return saved;
        });

        timeLoggingService.updateLog("log123", updateRequest, "employee123");

        verify(timeLogRepository, times(1)).save(any(TimeLog.class));
    }

    @Test
    void testDeleteLog_Success() {
        when(timeLogRepository.findByIdAndEmployeeId("log123", "employee123"))
                .thenReturn(Optional.of(testTimeLog));
        doNothing().when(timeLogRepository).delete(any(TimeLog.class));

        timeLoggingService.deleteLog("log123", "employee123");

        verify(timeLogRepository, times(1)).delete(testTimeLog);
    }

    @Test
    void testDeleteLog_AccessDenied() {
        when(timeLogRepository.findByIdAndEmployeeId("log123", "wrongEmployee"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeLoggingService.deleteLog("log123", "wrongEmployee"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testGetEmployeeSummary_Month() {
        TimeLog log1 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service1")
                .projectId("project1")
                .hours(8.0)
                .date(LocalDate.of(2025, 11, 15))
                .build();

        TimeLog log2 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service1")
                .projectId("project2")
                .hours(6.0)
                .date(LocalDate.of(2025, 11, 20))
                .build();

        TimeLog log3 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service2")
                .projectId("project1")
                .hours(4.0)
                .date(LocalDate.of(2025, 11, 25))
                .build();

        when(timeLogRepository.findByEmployeeIdAndDateBetween(
                eq("employee123"),
                eq(LocalDate.of(2025, 11, 1)),
                eq(LocalDate.of(2025, 11, 30))
        )).thenReturn(Arrays.asList(log1, log2, log3));

        TimeLogSummaryResponse summary = timeLoggingService.getEmployeeSummary(
                "employee123", "month", "2025-11");

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalHours()).isEqualTo(18.0);
        assertThat(summary.getCount()).isEqualTo(3);
        assertThat(summary.getByService()).hasSize(2);
        assertThat(summary.getByService().get("service1")).isEqualTo(14.0);
        assertThat(summary.getByService().get("service2")).isEqualTo(4.0);
        assertThat(summary.getByProject()).hasSize(2);
        assertThat(summary.getByProject().get("project1")).isEqualTo(12.0);
        assertThat(summary.getByProject().get("project2")).isEqualTo(6.0);
    }

    @Test
    void testGetEmployeeSummary_Week() {
        TimeLog log1 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service1")
                .hours(8.0)
                .date(LocalDate.of(2025, 11, 21))
                .build();

        TimeLog log2 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service1")
                .hours(7.0)
                .date(LocalDate.of(2025, 11, 22))
                .build();

        when(timeLogRepository.findByEmployeeIdAndDateBetween(
                eq("employee123"),
                eq(LocalDate.of(2025, 11, 21)),
                eq(LocalDate.of(2025, 11, 27))
        )).thenReturn(Arrays.asList(log1, log2));

        TimeLogSummaryResponse summary = timeLoggingService.getEmployeeSummary(
                "employee123", "week", "2025-11-21");

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalHours()).isEqualTo(15.0);
        assertThat(summary.getCount()).isEqualTo(2);
    }

    @Test
    void testGetEmployeeSummary_InvalidPeriod() {
        assertThatThrownBy(() -> timeLoggingService.getEmployeeSummary(
                "employee123", "invalid", "2025-11-21"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid period");
    }

    @Test
    void testGetEmployeeSummary_EmptyResult() {
        when(timeLogRepository.findByEmployeeIdAndDateBetween(
                anyString(), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(Arrays.asList());

        TimeLogSummaryResponse summary = timeLoggingService.getEmployeeSummary(
                "employee123", "month", "2025-11");

        assertThat(summary.getTotalHours()).isEqualTo(0.0);
        assertThat(summary.getCount()).isEqualTo(0);
        assertThat(summary.getByService()).isEmpty();
        assertThat(summary.getByProject()).isEmpty();
    }
}

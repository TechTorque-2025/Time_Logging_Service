package com.techtorque.time_logging_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.service.TimeLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TimeLogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TimeLogService timeLogService;

    private TimeLogResponse testResponse;

    @BeforeEach
    void setUp() {
        testResponse = new TimeLogResponse();
        testResponse.setId("log123");
        testResponse.setEmployeeId("employee123");
        testResponse.setServiceId("service456");
        testResponse.setProjectId("project789");
        testResponse.setHours(8.0);
        testResponse.setDate(LocalDate.of(2025, 11, 21));
        testResponse.setDescription("Worked on feature");
        testResponse.setWorkType("Development");
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testCreateTimeLog_Success() throws Exception {
        TimeLogRequest request = new TimeLogRequest();
        request.setServiceId("service456");
        request.setProjectId("project789");
        request.setHours(8.0);
        request.setDate(LocalDate.of(2025, 11, 21));
        request.setDescription("Worked on feature");
        request.setWorkType("Development");

        when(timeLogService.createTimeLog(anyString(), any(TimeLogRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/time-logs")
                        .header("X-User-Subject", "employee123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("log123"))
                .andExpect(jsonPath("$.hours").value(8.0));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetMyTimeLogs_Employee() throws Exception {
        when(timeLogService.getAllTimeLogsByEmployee("employee123"))
                .thenReturn(Arrays.asList(testResponse));

        mockMvc.perform(get("/time-logs")
                        .header("X-User-Subject", "employee123")
                        .header("X-User-Roles", "ROLE_EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("log123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetMyTimeLogs_Admin() throws Exception {
        when(timeLogService.getAllTimeLogs())
                .thenReturn(Arrays.asList(testResponse));

        mockMvc.perform(get("/time-logs")
                        .header("X-User-Subject", "admin123")
                        .header("X-User-Roles", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetTimeLogById_Success() throws Exception {
        when(timeLogService.getTimeLogByIdWithAuthorization(anyString(), anyString(), anyString()))
                .thenReturn(testResponse);

        mockMvc.perform(get("/time-logs/{logId}", "log123")
                        .header("X-User-Subject", "employee123")
                        .header("X-User-Role", "ROLE_EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("log123"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testUpdateTimeLog_Success() throws Exception {
        TimeLogUpdateRequest updateRequest = new TimeLogUpdateRequest();
        updateRequest.setHours(10.0);
        updateRequest.setDescription("Updated description");

        when(timeLogService.updateTimeLogWithAuthorization(anyString(), anyString(), any(TimeLogUpdateRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/time-logs/{logId}", "log123")
                        .header("X-User-Subject", "employee123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("log123"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testDeleteTimeLog_Success() throws Exception {
        mockMvc.perform(delete("/time-logs/{logId}", "log123")
                        .header("X-User-Subject", "employee123")
                        .header("X-User-Roles", "ROLE_EMPLOYEE"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetTimeLogsForService_Success() throws Exception {
        when(timeLogService.getTimeLogsByServiceId("service456"))
                .thenReturn(Arrays.asList(testResponse));

        mockMvc.perform(get("/time-logs/service/{serviceId}", "service456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].serviceId").value("service456"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetTimeLogsForProject_Success() throws Exception {
        when(timeLogService.getTimeLogsByProjectId("project789"))
                .thenReturn(Arrays.asList(testResponse));

        mockMvc.perform(get("/time-logs/project/{projectId}", "project789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].projectId").value("project789"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetEmployeeStats_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("employeeId", "employee123");
        stats.put("totalHours", 40.0);
        stats.put("totalLogs", 5);

        when(timeLogService.getEmployeeStatistics("employee123"))
                .thenReturn(stats);

        mockMvc.perform(get("/time-logs/stats")
                        .header("X-User-Subject", "employee123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("employee123"))
                .andExpect(jsonPath("$.totalHours").value(40.0));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetMyTimeLogs_WithDateRange() throws Exception {
        when(timeLogService.getTimeLogsByDateRange(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(testResponse));

        mockMvc.perform(get("/time-logs")
                        .header("X-User-Subject", "employee123")
                        .header("X-User-Roles", "ROLE_EMPLOYEE")
                        .param("from", "2025-11-01")
                        .param("to", "2025-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

package com.techtorque.time_logging_service.dto.response;

import java.util.Map;

public class TimeLogSummaryResponse {
    private String employeeId;
    private String period;
    private double totalHours;
    private int count;
    private Map<String, Double> byService; // serviceId -> hours
    private Map<String, Double> byProject; // projectId -> hours

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<String, Double> getByService() {
        return byService;
    }

    public void setByService(Map<String, Double> byService) {
        this.byService = byService;
    }

    public Map<String, Double> getByProject() {
        return byProject;
    }

    public void setByProject(Map<String, Double> byProject) {
        this.byProject = byProject;
    }
}


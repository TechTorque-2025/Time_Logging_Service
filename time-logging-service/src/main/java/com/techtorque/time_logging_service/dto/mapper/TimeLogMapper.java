package com.techtorque.time_logging_service.dto.mapper;

import com.techtorque.time_logging_service.dto.request.TimeLogRequest;
import com.techtorque.time_logging_service.dto.request.TimeLogUpdateRequest;
import com.techtorque.time_logging_service.dto.response.TimeLogResponse;
import com.techtorque.time_logging_service.entity.TimeLog;

import java.util.Objects;

public class TimeLogMapper {

    public static TimeLog toEntity(TimeLogRequest req) {
        if (req == null) return null;
        TimeLog e = new TimeLog();
        e.setServiceId(req.getServiceId());
        e.setProjectId(req.getProjectId());
        e.setHours(req.getHours());
        e.setDate(req.getDate());
        e.setDescription(req.getDescription());
        e.setWorkType(req.getWorkType());
        return e;
    }

    public static void applyUpdate(TimeLogUpdateRequest update, TimeLog e) {
        if (update == null || e == null) return;
        if (Objects.nonNull(update.getServiceId())) e.setServiceId(update.getServiceId());
        if (Objects.nonNull(update.getProjectId())) e.setProjectId(update.getProjectId());
        if (Objects.nonNull(update.getHours())) e.setHours(update.getHours());
        if (Objects.nonNull(update.getDate())) e.setDate(update.getDate());
        if (Objects.nonNull(update.getDescription())) e.setDescription(update.getDescription());
        if (Objects.nonNull(update.getWorkType())) e.setWorkType(update.getWorkType());
    }

    public static TimeLogResponse toResponse(TimeLog e) {
        if (e == null) return null;
        TimeLogResponse r = new TimeLogResponse();
        r.setId(e.getId());
        r.setEmployeeId(e.getEmployeeId());
        r.setServiceId(e.getServiceId());
        r.setProjectId(e.getProjectId());
        r.setHours(e.getHours());
        r.setDate(e.getDate());
        r.setDescription(e.getDescription());
        r.setWorkType(e.getWorkType());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }
}


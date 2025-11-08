package com.techtorque.time_logging_service.events;

import com.techtorque.time_logging_service.entity.TimeLog;

public interface TimeLogEventPublisher {
    void publishTimeLogged(TimeLog timeLog);
}


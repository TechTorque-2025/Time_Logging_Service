package com.techtorque.time_logging_service.events;

import com.techtorque.time_logging_service.entity.TimeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoopTimeLogEventPublisher implements TimeLogEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NoopTimeLogEventPublisher.class);

    @Override
    public void publishTimeLogged(TimeLog timeLog) {
        // No-op for now, just log for visibility
        if (timeLog != null) {
            log.debug("Noop publish time logged event for id={} employeeId={}", timeLog.getId(), timeLog.getEmployeeId());
        }
    }
}


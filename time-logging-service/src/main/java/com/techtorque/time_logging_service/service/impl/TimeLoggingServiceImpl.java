package com.techtorque.time_logging_service.service.impl;

import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import com.techtorque.time_logging_service.service.TimeLoggingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TimeLoggingServiceImpl implements TimeLoggingService {

  private final TimeLogRepository timeLogRepository;

  public TimeLoggingServiceImpl(TimeLogRepository timeLogRepository) {
    this.timeLogRepository = timeLogRepository;
  }

  @Override
  public TimeLog logWorkTime(/* TimeLogRequestDto dto, */ String employeeId) {
    // TODO: Developer will implement this logic.
    // 1. Create a new TimeLog entity from the DTO.
    // 2. Set the employeeId from the method parameter.
    // 3. Save the new log using timeLogRepository.save().
    // 4. IMPORTANT: After saving, consider emitting an event (e.g., to RabbitMQ)
    //    with the serviceId and hours logged. The Project Management service can
    //    listen for this event to update its own 'progress' or 'hoursLogged' fields.
    return null;
  }

  @Override
  public List<TimeLog> getLogsForService(String serviceId) {
    // TODO: Developer will implement this logic.
    // 1. Call timeLogRepository.findByServiceId(serviceId).
    // 2. Return the list. This method will be called by the Project Management service.
    return List.of();
  }
}
package com.techtorque.time_logging_service.config;

import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Data seeder to populate sample time log entries for testing
 * Only runs in 'dev' profile to avoid polluting production data
 *
 * Similar pattern to Authentication service DataSeeder
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) throws Exception {
        // Only seed data in development profile
        if (!isDevProfile()) {
            logger.info("Not in 'dev' profile. Skipping time log data seeding.");
            return;
        }

        // Check if data already exists to avoid duplicates
        if (timeLogRepository.count() > 0) {
            logger.info("Time logs already exist in database ({} entries). Skipping seeding.",
                       timeLogRepository.count());
            return;
        }

        logger.info("Starting time log data seeding for development environment...");
        seedSampleTimeLogs();
        logger.info("Time log data seeding completed successfully!");
    }

    /**
     * Check if 'dev' profile is active
     */
    private boolean isDevProfile() {
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Seed sample time log entries for testing
     * Creates realistic test data for 3 employees over the past 7 days
     * 
     * UPDATED: Now uses SharedConstants for consistent employee IDs that match Auth service
     */
    private void seedSampleTimeLogs() {
        // Use actual employee UUIDs from Auth service (via SharedConstants)
        String[] employees = {
            SharedConstants.UserIds.EMPLOYEE_1,
            SharedConstants.UserIds.EMPLOYEE_2,
            SharedConstants.UserIds.EMPLOYEE_3
        };

        // Work types from SharedConstants
        String[] workTypes = {
            SharedConstants.WorkTypes.DIAGNOSTIC,
            SharedConstants.WorkTypes.REPAIR,
            SharedConstants.WorkTypes.MAINTENANCE,
            SharedConstants.WorkTypes.INSTALLATION,
            SharedConstants.WorkTypes.INSPECTION,
            SharedConstants.WorkTypes.TESTING
        };

        // Use service and project IDs from SharedConstants
        String[] projects = {
            SharedConstants.ProjectIds.PROJECT_1,
            SharedConstants.ProjectIds.PROJECT_2,
            SharedConstants.ProjectIds.PROJECT_3,
            SharedConstants.ProjectIds.PROJECT_4,
            SharedConstants.ProjectIds.PROJECT_5
        };

        String[] services = {
            SharedConstants.ServiceIds.SERVICE_1,
            SharedConstants.ServiceIds.SERVICE_2,
            SharedConstants.ServiceIds.SERVICE_3,
            SharedConstants.ServiceIds.SERVICE_4,
            SharedConstants.ServiceIds.SERVICE_5
        };

        // Sample descriptions
        String[] descriptions = {
            "Diagnosed engine issue and identified faulty spark plugs",
            "Completed brake pad replacement on front wheels",
            "Performed routine oil change and filter replacement",
            "Installed new alternator and tested charging system",
            "Conducted comprehensive vehicle safety inspection",
            "Repaired exhaust system leak at manifold joint",
            "Performed wheel alignment and tire rotation"
        };

        LocalDate today = LocalDate.now();
        int logCount = 0;

        // Create logs for the past 7 days (including today)
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate workDate = today.minusDays(dayOffset);

            // Skip weekends (Saturday = 6, Sunday = 7 in ISO)
            int dayOfWeek = workDate.getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                logger.debug("Skipping weekend: {}", workDate);
                continue;
            }

            // Each employee logs time
            for (String employeeId : employees) {
                // Morning session (3-5 hours)
                double morningHours = 3.0 + (Math.random() * 2.0); // Random between 3-5 hours
                TimeLog morningLog = TimeLog.builder()
                        .employeeId(employeeId)
                        .projectId(projects[logCount % projects.length])
                        .serviceId(services[logCount % services.length])
                        .hours(Math.round(morningHours * 2) / 2.0) // Round to nearest 0.5
                        .date(workDate)
                        .description(descriptions[logCount % descriptions.length])
                        .workType(workTypes[logCount % workTypes.length])
                        .build();

                timeLogRepository.save(morningLog);
                logCount++;

                // Afternoon session (3-5 hours)
                double afternoonHours = 3.0 + (Math.random() * 2.0);
                TimeLog afternoonLog = TimeLog.builder()
                        .employeeId(employeeId)
                        .projectId(projects[logCount % projects.length])
                        .serviceId(services[logCount % services.length])
                        .hours(Math.round(afternoonHours * 2) / 2.0)
                        .date(workDate)
                        .description(descriptions[logCount % descriptions.length])
                        .workType(workTypes[(logCount + 1) % workTypes.length])
                        .build();

                timeLogRepository.save(afternoonLog);
                logCount++;

                logger.debug("Created {} time logs for employee {} on {}",
                           2, employeeId, workDate);
            }
        }

        long totalCount = timeLogRepository.count();
        logger.info("✅ Successfully seeded {} time log entries across {} employees",
                   totalCount, employees.length);

        // Log summary statistics
        for (String empId : employees) {
            Double totalHours = timeLogRepository.getTotalHoursByEmployeeId(empId);
            long empLogCount = timeLogRepository.findByEmployeeId(empId).size();
            logger.info("   Employee {}: {} logs, {} hours total",
                       empId, empLogCount, String.format("%.1f", totalHours));
        }
    }
}


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
     */
    private void seedSampleTimeLogs() {
        // Sample employee IDs (these would correspond to real users from Auth service)
        String[] employees = {"EMP001", "EMP002", "EMP003"};

        // Sample work types
        String[] workTypes = {
            "Development",
            "Testing",
            "Code Review",
            "Meetings",
            "Documentation",
            "Bug Fixing",
            "Planning"
        };

        // Sample project IDs
        String[] projects = {
            "PRJ001", "PRJ002", "PRJ003", "PRJ004", "PRJ005"
        };

        // Sample service IDs
        String[] services = {
            "SRV001", "SRV002", "SRV003", "SRV004", "SRV005"
        };

        // Sample descriptions
        String[] descriptions = {
            "Implemented new feature for customer dashboard",
            "Fixed critical bug in payment processing",
            "Reviewed pull requests from team members",
            "Attended daily standup and sprint planning",
            "Updated API documentation",
            "Refactored legacy code for better performance",
            "Set up CI/CD pipeline for automated testing"
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


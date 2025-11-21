package com.techtorque.time_logging_service.repository;

import com.techtorque.time_logging_service.entity.TimeLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TimeLogRepositoryTest {

    @Autowired
    private TimeLogRepository timeLogRepository;

    private TimeLog testTimeLog;

    @BeforeEach
    void setUp() {
        timeLogRepository.deleteAll();

        testTimeLog = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service456")
                .projectId("project789")
                .hours(8.0)
                .date(LocalDate.of(2025, 11, 21))
                .description("Worked on feature implementation")
                .workType("Development")
                .build();
    }

    @Test
    void testSaveTimeLog() {
        TimeLog saved = timeLogRepository.save(testTimeLog);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmployeeId()).isEqualTo("employee123");
        assertThat(saved.getHours()).isEqualTo(8.0);
        assertThat(saved.getDate()).isEqualTo(LocalDate.of(2025, 11, 21));
    }

    @Test
    void testFindById() {
        timeLogRepository.save(testTimeLog);

        Optional<TimeLog> found = timeLogRepository.findById(testTimeLog.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmployeeId()).isEqualTo("employee123");
    }

    @Test
    void testFindByEmployeeId() {
        TimeLog timeLog2 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service999")
                .hours(4.0)
                .date(LocalDate.of(2025, 11, 22))
                .description("Code review")
                .workType("Review")
                .build();

        timeLogRepository.save(testTimeLog);
        timeLogRepository.save(timeLog2);

        List<TimeLog> employeeLogs = timeLogRepository.findByEmployeeId("employee123");

        assertThat(employeeLogs).hasSize(2);
        assertThat(employeeLogs).allMatch(log -> log.getEmployeeId().equals("employee123"));
    }

    @Test
    void testFindByServiceId() {
        timeLogRepository.save(testTimeLog);

        List<TimeLog> serviceLogs = timeLogRepository.findByServiceId("service456");

        assertThat(serviceLogs).hasSize(1);
        assertThat(serviceLogs.get(0).getServiceId()).isEqualTo("service456");
    }

    @Test
    void testFindByProjectId() {
        timeLogRepository.save(testTimeLog);

        List<TimeLog> projectLogs = timeLogRepository.findByProjectId("project789");

        assertThat(projectLogs).hasSize(1);
        assertThat(projectLogs.get(0).getProjectId()).isEqualTo("project789");
    }

    @Test
    void testFindByIdAndEmployeeId() {
        timeLogRepository.save(testTimeLog);

        Optional<TimeLog> found = timeLogRepository.findByIdAndEmployeeId(
                testTimeLog.getId(), "employee123");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(testTimeLog.getId());
    }

    @Test
    void testFindByIdAndEmployeeId_DifferentEmployee() {
        timeLogRepository.save(testTimeLog);

        Optional<TimeLog> found = timeLogRepository.findByIdAndEmployeeId(
                testTimeLog.getId(), "differentEmployee");

        assertThat(found).isEmpty();
    }

    @Test
    void testFindByEmployeeIdAndDateBetween() {
        TimeLog log1 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service1")
                .hours(8.0)
                .date(LocalDate.of(2025, 11, 20))
                .description("Day 1")
                .build();

        TimeLog log2 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service2")
                .hours(7.0)
                .date(LocalDate.of(2025, 11, 21))
                .description("Day 2")
                .build();

        TimeLog log3 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service3")
                .hours(6.0)
                .date(LocalDate.of(2025, 11, 25))
                .description("Day 3 - outside range")
                .build();

        timeLogRepository.save(log1);
        timeLogRepository.save(log2);
        timeLogRepository.save(log3);

        List<TimeLog> logsInRange = timeLogRepository.findByEmployeeIdAndDateBetween(
                "employee123",
                LocalDate.of(2025, 11, 20),
                LocalDate.of(2025, 11, 22)
        );

        assertThat(logsInRange).hasSize(2);
        assertThat(logsInRange).noneMatch(log -> log.getDate().equals(LocalDate.of(2025, 11, 25)));
    }

    @Test
    void testGetTotalHoursByEmployeeId() {
        TimeLog log1 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service1")
                .hours(8.0)
                .date(LocalDate.of(2025, 11, 20))
                .description("Day 1")
                .build();

        TimeLog log2 = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service2")
                .hours(7.5)
                .date(LocalDate.of(2025, 11, 21))
                .description("Day 2")
                .build();

        timeLogRepository.save(log1);
        timeLogRepository.save(log2);

        Double totalHours = timeLogRepository.getTotalHoursByEmployeeId("employee123");

        assertThat(totalHours).isEqualTo(15.5);
    }

    @Test
    void testGetTotalHoursByEmployeeId_NoLogs() {
        Double totalHours = timeLogRepository.getTotalHoursByEmployeeId("nonexistent");

        assertThat(totalHours).isNull();
    }

    @Test
    void testUpdateTimeLog() {
        timeLogRepository.save(testTimeLog);

        testTimeLog.setHours(10.0);
        testTimeLog.setDescription("Updated description");
        TimeLog updated = timeLogRepository.save(testTimeLog);

        assertThat(updated.getHours()).isEqualTo(10.0);
        assertThat(updated.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void testDeleteTimeLog() {
        timeLogRepository.save(testTimeLog);
        String logId = testTimeLog.getId();

        timeLogRepository.deleteById(logId);

        Optional<TimeLog> deleted = timeLogRepository.findById(logId);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testTimeLogWithNullProjectId() {
        TimeLog logWithoutProject = TimeLog.builder()
                .employeeId("employee123")
                .serviceId("service456")
                .hours(5.0)
                .date(LocalDate.of(2025, 11, 21))
                .description("Service only work")
                .build();

        TimeLog saved = timeLogRepository.save(logWithoutProject);

        assertThat(saved.getProjectId()).isNull();
        assertThat(saved.getServiceId()).isNotNull();
    }
}

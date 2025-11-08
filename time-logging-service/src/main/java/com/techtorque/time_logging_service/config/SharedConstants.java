package com.techtorque.time_logging_service.config;

/**
 * Shared constants for cross-service data consistency
 * 
 * These should match the Auth service seeded USERNAMES (not UUIDs).
 * The Gateway forwards X-User-Subject header with USERNAME values, so all
 * services should use usernames as user identifiers.
 * 
 * IMPORTANT: Keep these constants in sync with the Authentication Service DataSeeder
 * 
 * Usage in seeders and test data to maintain consistent references across:
 * - Authentication Service (User IDs)
 * - Vehicle Service (Vehicle IDs linked to customers)
 * - Appointment Service (Appointments linked to customers and employees)
 * - Service/Project Service (Services assigned to employees)
 * - Time Logging Service (Time logs linked to employees)
 * - Payment Service (Payments linked to customers)
 */
public class SharedConstants {

    /**
     * User IDs from Authentication Service - using USERNAMES
     * These match the usernames created by the Auth service DataSeeder
     */
    public static final class UserIds {
        // Super Admin
        public static final String SUPER_ADMIN = "superadmin";
        
        // Regular Admin
        public static final String ADMIN = "admin";
        
        // Employees (Auth service only seeds one employee user)
        public static final String EMPLOYEE_1 = "employee";
        public static final String EMPLOYEE_2 = "employee";
        public static final String EMPLOYEE_3 = "employee";
        
        // Customers
        public static final String CUSTOMER_1 = "customer";
        public static final String CUSTOMER_2 = "testuser";
        public static final String CUSTOMER_3 = "demo";
    }

    /**
     * Vehicle IDs from Vehicle Service
     * These IDs should match the vehicles created in the Vehicle Service DataSeeder
     */
    public static final class VehicleIds {
        // Customer 1 (customer) vehicles
        public static final String VEHICLE_1 = "VEH-2022-TOYOTA-CAMRY-0001";
        public static final String VEHICLE_2 = "VEH-2021-HONDA-ACCORD-0002";
        
        // Customer 2 (testuser) vehicles
        public static final String VEHICLE_3 = "VEH-2023-BMW-X5-0003";
        public static final String VEHICLE_4 = "VEH-2020-MERCEDES-C300-0004";
        
        // Customer 3 (demo) vehicles
        public static final String VEHICLE_5 = "VEH-2022-NISSAN-ALTIMA-0005";
        public static final String VEHICLE_6 = "VEH-2019-MAZDA-CX5-0006";
    }    /**
     * Service Type IDs from Admin/Appointment Service
     */
    public static final class ServiceTypeIds {
        public static final String OIL_CHANGE = "ST-001";
        public static final String BRAKE_SERVICE = "ST-002";
        public static final String TIRE_ROTATION = "ST-003";
        public static final String GENERAL_INSPECTION = "ST-004";
        public static final String ENGINE_DIAGNOSTIC = "ST-005";
    }

    /**
     * Service IDs (work orders) from Service Management
     */
    public static final class ServiceIds {
        public static final String SERVICE_1 = "SRV-001";
        public static final String SERVICE_2 = "SRV-002";
        public static final String SERVICE_3 = "SRV-003";
        public static final String SERVICE_4 = "SRV-004";
        public static final String SERVICE_5 = "SRV-005";
    }

    /**
     * Project IDs (custom modifications) from Service Management
     */
    public static final class ProjectIds {
        public static final String PROJECT_1 = "PRJ-001";
        public static final String PROJECT_2 = "PRJ-002";
        public static final String PROJECT_3 = "PRJ-003";
        public static final String PROJECT_4 = "PRJ-004";
        public static final String PROJECT_5 = "PRJ-005";
    }

    /**
     * Work type categories for time logs
     */
    public static final class WorkTypes {
        public static final String DIAGNOSTIC = "Diagnostic";
        public static final String REPAIR = "Repair";
        public static final String MAINTENANCE = "Maintenance";
        public static final String INSTALLATION = "Installation";
        public static final String INSPECTION = "Inspection";
        public static final String TESTING = "Testing";
        public static final String CONSULTATION = "Consultation";
        public static final String DOCUMENTATION = "Documentation";
    }

    /**
     * Employee roles (for reference)
     */
    public static final class Roles {
        public static final String SUPER_ADMIN = "SUPER_ADMIN";
        public static final String ADMIN = "ADMIN";
        public static final String EMPLOYEE = "EMPLOYEE";
        public static final String CUSTOMER = "CUSTOMER";
    }
}

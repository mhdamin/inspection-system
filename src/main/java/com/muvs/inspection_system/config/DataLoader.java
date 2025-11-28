package com.muvs.inspection_system.config;

import com.muvs.inspection_system.entity.*;
import com.muvs.inspection_system.enums.ChangeType;
import com.muvs.inspection_system.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VehicleRepository vehicleRepository;
    private final ChecklistRepository checklistRepository;
    private final VehicleChangeLogRepository vehicleChangeLogRepository;

    public DataLoader(UserRepository userRepository,
                     RoleRepository roleRepository,
                     PasswordEncoder passwordEncoder,
                     VehicleRepository vehicleRepository,
                     ChecklistRepository checklistRepository,
                     VehicleChangeLogRepository vehicleChangeLogRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.vehicleRepository = vehicleRepository;
        this.checklistRepository = checklistRepository;
        this.vehicleChangeLogRepository = vehicleChangeLogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data loading...");

        // Create Roles
        Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        Role userRole = roleRepository.save(Role.builder().name("ROLE_USER").build());

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        // Create Users
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(adminRoles)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .roles(userRoles)
                .build());

        log.info("Created 2 users (admin/user)");

        // Create Sample Vehicles
        createSampleVehicles();

        log.info("Data loading completed successfully!");
    }

    private void createSampleVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();

        // Toyota vehicles
        vehicles.add(createVehicle("SGX1234A", "Camry", "Toyota", 2023, "Available"));
        vehicles.add(createVehicle("SGX2345B", "Corolla", "Toyota", 2024, "Available"));
        vehicles.add(createVehicle("SGX3456C", "RAV4", "Toyota", 2023, "Rented"));
        vehicles.add(createVehicle("SGX4567D", "Vios", "Toyota", 2022, "Available"));
        vehicles.add(createVehicle("SGX5678E", "Alphard", "Toyota", 2024, "Maintenance"));

        // Honda vehicles
        vehicles.add(createVehicle("SGY1111F", "Civic", "Honda", 2023, "Available"));
        vehicles.add(createVehicle("SGY2222G", "CR-V", "Honda", 2024, "Rented"));
        vehicles.add(createVehicle("SGY3333H", "Accord", "Honda", 2023, "Available"));
        vehicles.add(createVehicle("SGY4444I", "Jazz", "Honda", 2022, "Available"));
        vehicles.add(createVehicle("SGY5555J", "HR-V", "Honda", 2024, "Rented"));

        // Ford vehicles
        vehicles.add(createVehicle("SGZ6666K", "Mustang", "Ford", 2024, "Available"));
        vehicles.add(createVehicle("SGZ7777L", "Ranger", "Ford", 2023, "Rented"));
        vehicles.add(createVehicle("SGZ8888M", "Transit", "Ford", 2023, "Maintenance"));
        vehicles.add(createVehicle("SGZ9999N", "Explorer", "Ford", 2024, "Available"));

        // Tesla vehicles
        vehicles.add(createVehicle("SGA1000P", "Model 3", "Tesla", 2024, "Available"));
        vehicles.add(createVehicle("SGA2000Q", "Model Y", "Tesla", 2024, "Rented"));
        vehicles.add(createVehicle("SGA3000R", "Model S", "Tesla", 2023, "Available"));

        // Nissan vehicles
        vehicles.add(createVehicle("SGB4000S", "Altima", "Nissan", 2023, "Available"));
        vehicles.add(createVehicle("SGB5000T", "Qashqai", "Nissan", 2024, "Rented"));
        vehicles.add(createVehicle("SGB6000U", "X-Trail", "Nissan", 2023, "Available"));
        vehicles.add(createVehicle("SGB7000V", "Leaf", "Nissan", 2024, "Maintenance"));

        // Mazda vehicles
        vehicles.add(createVehicle("SGC8000W", "CX-5", "Mazda", 2024, "Available"));
        vehicles.add(createVehicle("SGC9000X", "Mazda3", "Mazda", 2023, "Available"));
        vehicles.add(createVehicle("SGC1001Y", "CX-9", "Mazda", 2024, "Rented"));

        // Mercedes vehicles
        vehicles.add(createVehicle("SGD2002Z", "C-Class", "Mercedes-Benz", 2024, "Available"));
        vehicles.add(createVehicle("SGD3003A", "E-Class", "Mercedes-Benz", 2023, "Rented"));
        vehicles.add(createVehicle("SGD4004B", "GLC", "Mercedes-Benz", 2024, "Available"));

        // BMW vehicles
        vehicles.add(createVehicle("SGE5005C", "3 Series", "BMW", 2024, "Available"));
        vehicles.add(createVehicle("SGE6006D", "X5", "BMW", 2023, "Maintenance"));
        vehicles.add(createVehicle("SGE7007E", "5 Series", "BMW", 2024, "Available"));

        // Save all vehicles
        vehicleRepository.saveAll(vehicles);
        log.info("Created {} sample vehicles", vehicles.size());

        // Create sample checklists for some rented vehicles
        createSampleChecklists(vehicles);
    }

    private Vehicle createVehicle(String plateNumber, String model, String manufacturer, Integer year, String status) {
        return Vehicle.builder()
                .plateNumber(plateNumber)
                .model(model)
                .manufacturer(manufacturer)
                .year(year)
                .status(status)
                .build();
    }

    private void createSampleChecklists(List<Vehicle> vehicles) {
        List<Checklist> checklists = new ArrayList<>();
        List<VehicleChangeLog> changeLogs = new ArrayList<>();

        int checklistCounter = 1000;

        // Create checklists for rented vehicles
        for (Vehicle vehicle : vehicles) {
            if ("Rented".equals(vehicle.getStatus())) {
                Checklist checklist = Checklist.builder()
                        .checklistNumber("CHK-2025-" + checklistCounter++)
                        .vehicle(vehicle)
                        .rentalStartDate(LocalDate.now().minusDays(7))
                        .rentalEndDate(LocalDate.now().plusDays(7))
                        .customerName("Customer " + vehicle.getPlateNumber().substring(3, 7))
                        .customerPhone("+65 9" + vehicle.getPlateNumber().substring(3, 7))
                        .staffName("John Smith")
                        .rentalType("DAILY")
                        .build();

                checklists.add(checklist);

                // Create activity log for this rental
                VehicleChangeLog changeLog = VehicleChangeLog.builder()
                        .checklist(checklist)
                        .changeType(ChangeType.TEMPORARY)
                        .newVehiclePlate(vehicle.getPlateNumber())
                        .reason("New rental initiated")
                        .timestamp(LocalDateTime.now().minusDays(7))
                        .staffName("John Smith")
                        .build();

                changeLogs.add(changeLog);
            }
        }

        // Create some checklists for maintenance vehicles
        for (Vehicle vehicle : vehicles) {
            if ("Maintenance".equals(vehicle.getStatus())) {
                Checklist checklist = Checklist.builder()
                        .checklistNumber("CHK-2025-" + checklistCounter++)
                        .vehicle(vehicle)
                        .rentalStartDate(LocalDate.now().minusDays(3))
                        .rentalEndDate(LocalDate.now().minusDays(1))
                        .customerName("Previous Customer")
                        .customerPhone("+65 91234567")
                        .staffName("Jane Doe")
                        .rentalType("WEEKLY")
                        .build();

                checklists.add(checklist);

                // Create activity log for maintenance
                VehicleChangeLog changeLog = VehicleChangeLog.builder()
                        .checklist(checklist)
                        .changeType(ChangeType.RETURN)
                        .newVehiclePlate(vehicle.getPlateNumber())
                        .reason("Vehicle sent for scheduled maintenance after rental return")
                        .timestamp(LocalDateTime.now().minusDays(1))
                        .staffName("Jane Doe")
                        .build();

                changeLogs.add(changeLog);
            }
        }

        checklistRepository.saveAll(checklists);
        log.info("Created {} sample checklists", checklists.size());

        vehicleChangeLogRepository.saveAll(changeLogs);
        log.info("Created {} activity log entries", changeLogs.size());
    }
}

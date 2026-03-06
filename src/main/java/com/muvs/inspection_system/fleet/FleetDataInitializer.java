package com.muvs.inspection_system.fleet;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
@RequiredArgsConstructor
public class FleetDataInitializer implements CommandLineRunner {

    private final FleetService fleetService;

    @Override
    public void run(String... args) {
        fleetService.seedIfEmpty();
    }
}

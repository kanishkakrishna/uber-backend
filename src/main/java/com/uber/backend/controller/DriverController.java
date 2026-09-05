package com.uber.backend.controller;
import com.uber.backend.dto.DriverRegistrationDTO;
import com.uber.backend.model.Driver;
import com.uber.backend.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Expose REST endpoints for driver operations.
@RequestMapping("/api/drivers") // Base route for driver APIs.
public class DriverController {

    // Delegate driver operations to the service layer.
    private final DriverService driverService;

    // Inject the driver service.
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // Register a new driver.
    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody DriverRegistrationDTO dto) {
        // Persist the DTO through the service layer.
        Driver savedDriver = driverService.registerDriver(dto);
        // Return the persisted driver, including database-generated fields.

        // Return the saved driver with HTTP 200.
        return ResponseEntity.ok(savedDriver);
    }
}
package com.uber.backend.service;

import com.uber.backend.dto.DriverRegistrationDTO;
import com.uber.backend.model.Driver;
import com.uber.backend.repository.DriverRepository;
import org.springframework.stereotype.Service;

@Service // Register this class as a Spring service.
public class DriverService {

    // Repository used to persist driver records.
    private final DriverRepository driverRepository;

    // Inject the driver repository.
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // Map the registration DTO to an entity and persist it.
    public Driver registerDriver(DriverRegistrationDTO dto) {

        Driver newDriver = new Driver();
        newDriver.setName(dto.getName());
        newDriver.setEmail(dto.getEmail());
        newDriver.setPhone(dto.getPhone());
        newDriver.setVehicleNumber(dto.getVehicleNumber());

        // Driver status defaults to OFFLINE in the model.

        // Persist and return the driver.
        return driverRepository.save(newDriver);
    }
}
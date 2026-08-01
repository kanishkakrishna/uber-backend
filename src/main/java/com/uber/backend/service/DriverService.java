package com.uber.backend.service;

import com.uber.backend.dto.DriverRegistrationDTO;
import com.uber.backend.model.Driver;
import com.uber.backend.repository.DriverRepository;
import org.springframework.stereotype.Service;

@Service // Ye Spring Boot ko batata hai ki ye class humara main "Brain" hai
public class DriverService {

    // Service ko Database (Godown) se baat karni padegi
    private final DriverRepository driverRepository;

    // Isko Constructor Injection bolte hain
    //bhai ye jo hai na notes me acche se diya hai doubt hai to pdh lo
    // ye bs ek tarah ka syntax hai connect krne ka dono ko ratna hi hota hai
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // Main Logic: DTO se data nikal kar Entity me daalna aur DB me save karna
    public Driver registerDriver(DriverRegistrationDTO dto) {

        Driver newDriver = new Driver();
        newDriver.setName(dto.getName());
        newDriver.setEmail(dto.getEmail());
        newDriver.setPhone(dto.getPhone());
        newDriver.setVehicleNumber(dto.getVehicleNumber());

        // Status by default OFFLINE rahega (wo model me already set hai)

        // Database me save kar do!
        return driverRepository.save(newDriver);
    }
}
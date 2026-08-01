package com.uber.backend.controller;
import com.uber.backend.dto.DriverRegistrationDTO;
import com.uber.backend.model.Driver;
import com.uber.backend.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Spring ko batata hai ki ye class API requests handle karegi
@RequestMapping("/api/drivers") // Is controller ka base URL
public class DriverController {

    // Controller ko Service (Brain) se baat karni hai
    private final DriverService driverService;

    //dekh yahan bhi connect kr rha hai bhai ye fixed syntax hai
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // POST request aayegi yahan pe
    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody DriverRegistrationDTO dto) {
        // Service ko DTO pass kiya, aur wahan se save hua Driver wapas liya
        Driver savedDriver = driverService.registerDriver(dto);
        // bhai upar wala line bahut important hai, jaanta hai yahan kya ho rha hai, dto
        //ka jo data tha wo bahut raw tha usme id ye sb kuch nhi tha hmlog kya kiye
        //driverservice jo class hm bnaye the service ka taaki aage chlke use ho uska use
        // kiye hum .registerDriver dekh wo wahi func hai service class me jo save krta hai
        //repo me data ko , wo save hua ab wo aise nhi save hota data base id ke saath
        // jo sahi id hoga usse save kr diya ab dekh hmlog wahi saved chiz ko collect kiye
        // savedDriver variable se, wo bhai dekh variable type ye wahi class hai jo model
        //type hai Driver database schema ka dekh dhyan se, wo fir hmlog liye taaki return
        //kr ske status 200 ok ke saath

        // Postman ko 200 OK status ke sath saved driver bhej diya
        return ResponseEntity.ok(savedDriver);
    }
}
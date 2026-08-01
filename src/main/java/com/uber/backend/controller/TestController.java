package com.uber.backend.controller; // Apna package name check kar lena

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Bhai, VIP entry mil gayi! Tera token aur bouncer dono ekdum mast kaam kar rahe hain. 🚀";
    }
}
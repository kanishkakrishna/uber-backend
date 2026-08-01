package com.uber.backend.dto;

import lombok.Data;

@Data // Lombok getter/setter khud bana dega
public class SignupRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
}
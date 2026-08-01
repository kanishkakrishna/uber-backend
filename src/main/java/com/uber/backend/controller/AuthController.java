package com.uber.backend.controller;

import com.uber.backend.dto.AuthRequest;
import com.uber.backend.dto.AuthResponse;
import com.uber.backend.dto.SignupRequest;
import com.uber.backend.model.User;
import com.uber.backend.repository.UserRepository;
import com.uber.backend.security.CustomUserDetailsService;
import com.uber.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // NAYA: REGISTRATION API
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {

        // Pehle check karo ki email already toh exist nahi karti
        if (userRepository.findByEmail(signupRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: Email pehle se registered hai bhai!");
        }

        // Naya user banakar entity me details daalo
        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPhone(signupRequest.getPhone());

        // Password encrypt karke save karo!
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepository.save(newUser); // Postgres me save kar diya

        return ResponseEntity.ok("Mubarak ho! User successfully register ho gaya!");
    }

    // PURANA: LOGIN API (Username ko ab Email mankar chalenge)
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {

        try {
            // Yahan AuthRequest ka username asal me user ki EMAIL hai
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Bhai, Email ya Password galat hai!", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
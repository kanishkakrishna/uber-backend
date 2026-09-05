package com.uber.backend.security;

import com.uber.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load the application user by email.
        com.uber.backend.model.User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Bhai, ye email DB me nahi mili: " + email);
        }

        // Adapt the application user to Spring Security's UserDetails.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // No roles or authorities are assigned yet.
        );
    }
}
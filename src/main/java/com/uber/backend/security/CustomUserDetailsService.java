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
        // Tere repo me findByEmail banaya hua hai, usi ko use karenge
        com.uber.backend.model.User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Bhai, ye email DB me nahi mili: " + email);
        }

        // Agar mil gaya, toh Spring Security ko uski details pakda do
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Authorities/Roles abhi khali rakhte hain
        );
    }
}
package com.uber.backend.repository;

import com.uber.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Ye khaali rahega. JpaRepository apne aap save(), findById(), findAll() de dega.

    // Custom query: Email se user dhoondhne ke liye (Spring iska SQL khud banayega!)
    User findByEmail(String email);
}
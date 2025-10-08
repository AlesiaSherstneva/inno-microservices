package com.innowise.authservice.repository;

import com.innowise.authservice.model.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, String> {
    boolean existsByPhoneNumber(String phoneNumber);
}
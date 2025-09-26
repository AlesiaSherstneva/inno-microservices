package com.innowise.userservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    private ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException userNotFound(Long id) {
        return new ResourceNotFoundException("User not found with id: " + id);
    }

    public static ResourceNotFoundException userNotFound(String email) {
        return new ResourceNotFoundException("User not found with email: " + email);
    }

    public static ResourceNotFoundException cardNotFound(Long id) {
        return new ResourceNotFoundException("Card not found with id: " + id);
    }
}
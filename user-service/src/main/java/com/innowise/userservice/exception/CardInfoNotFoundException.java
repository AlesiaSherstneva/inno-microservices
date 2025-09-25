package com.innowise.userservice.exception;

public class CardInfoNotFoundException extends RuntimeException {
    public CardInfoNotFoundException(Long id) {
        super("Card not found with id: " + id);
    }
}
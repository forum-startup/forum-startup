package org.example.forumstartup.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String entityName, String attribute, String value) {
        super(String.format("%s with %s '%s' already exists.", entityName, attribute, value));
    }
}
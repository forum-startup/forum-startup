package org.example.forumstartup.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s with id %d was not found", entityName, id));
    }
    public EntityNotFoundException(String entityName, String attribute, String value) {
        super(String.format("%s with %s '%s' was not found.", entityName, attribute, value));
    }
}

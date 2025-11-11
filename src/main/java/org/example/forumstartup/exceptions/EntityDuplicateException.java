package org.example.forumstartup.exceptions;

public class EntityDuplicateException extends RuntimeException {

    public EntityDuplicateException(String entityName, String attribute, String value) {
        super(String.format("%s with %s '%s' already exists.", entityName, attribute, value));
    }
}
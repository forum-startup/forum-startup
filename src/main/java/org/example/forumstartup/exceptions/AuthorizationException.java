package org.example.forumstartup.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String action, String resource) {
        super(String.format("You are not authorized to %s this %s.", action, resource));
    }
}

package org.example.forumstartup.spec;

import org.example.forumstartup.models.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {

    public static Specification<User> byUsername(String username) {
        return (root, query, cb) ->
                username == null || username.isBlank() ?
                        null :
                        cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> byEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank() ?
                        null :
                        cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> byFirstName(String firstName) {
        return (root, query, cb) ->
                firstName == null || firstName.isBlank() ?
                        null :
                        cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }
}

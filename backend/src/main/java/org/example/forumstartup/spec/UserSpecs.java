package org.example.forumstartup.spec;

import org.example.forumstartup.models.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {

    public static Specification<User> matchesAny(String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank())
                return null;

            String pattern = "%" + value.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("username")), pattern),
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }
}

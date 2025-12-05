package org.example.forumstartup.spec;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecs {

    public static Specification<Post> matchesAny(String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank())
                return null;

            String pattern = "%" + value.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("creator").get("username")), pattern),
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("content")), pattern)
            );
        };
    }
}

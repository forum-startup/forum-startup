package org.example.forumstartup.spec;

import org.example.forumstartup.models.Post;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecs {

    public static Specification<Post> byUsername(String username) {
        return (root, query, cb) ->
                username == null ? null :
                        cb.equal(root.join("creator").get("username"), username);
    }

    public static Specification<Post> byText(String text) {
        return (root, query, cb) ->
                text == null ? null :
                        cb.or(
                                cb.like(cb.lower(root.get("title")), "%" + text.toLowerCase() + "%"),
                                cb.like(cb.lower(root.get("content")), "%" + text.toLowerCase() + "%")
                        );
    }

    public static Specification<Post> byTag(String tag) {
        return (root, query, cb) ->
                tag == null ? null :
                        cb.equal(root.join("tags").get("name"), tag);
    }
}

package org.example.forumstartup.utils;

import org.springframework.data.domain.Sort;

public class PageableUtils {

    private PageableUtils() {}

    public static Sort.Order parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return new Sort.Order(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction =
                parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.DESC;

        return new Sort.Order(direction, field);
    }
}

package org.example.forumstartup.helpers;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static<T> List<T> trimToLimit(List<T> list, int limit) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        int max = Math.max(1, Math.min(limit, 50));
        List<T>trimmed = new ArrayList<>();
        for (int i = 0; i < list.size() && i < max; i++) {
            trimmed.add(list.get(i));
        }

        return trimmed;
    }
}

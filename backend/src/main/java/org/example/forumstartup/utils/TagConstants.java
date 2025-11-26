package org.example.forumstartup.utils;

public class TagConstants {

    private TagConstants() {}

    // Length limits
    public static final int TAG_MIN_LENGTH = 2;
    public static final int TAG_MAX_LENGTH = 32;

    // Patterns
    // Allowed while INPUTTING (DTO)
    public static final String TAG_ALLOWED_PATTERN = "^[a-zA-Z0-9\\- ]+$";

    // Allowed AFTER NORMALIZATION (Service)
    public static final String TAG_NORMALIZED_PATTERN = "^[a-z0-9\\-]+$";

    // Messages
    public static final String INVALID_TAG_LENGTH =
            "Tag length must be between %d and %d characters.";

    public static final String TAG_PATTERN_MESSAGE =
            "Tag may contain only letters, digits, spaces, and hyphens.";

    public static final String TAG_NORMALIZED_PATTERN_MESSAGE =
            "Tag contains invalid characters after normalization.";
}

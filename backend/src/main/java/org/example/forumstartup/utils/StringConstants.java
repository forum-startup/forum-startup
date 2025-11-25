package org.example.forumstartup.utils;

public class StringConstants {

    public static final String FIRST_NAME_SIZE_CONSTRAINT_MESSAGE = "First name must be between 4 and 32 symbols.";
    public static final String LAST_NAME_SIZE_CONSTRAINT_MESSAGE = "Last name must be between 4 and 32 symbols.";
    public static final String EMAIL_TYPE_CONSTRAINT_MESSAGE = "Invalid email format";

    public static final String TITLE_SIZE_CONSTRAINT_MESSAGE = "The title must be between 16 and 64 symbols.";
    public static final String CONTENT_SIZE_CONSTRAINT_MESSAGE = "The content must be between 32 symbols and 8192 symbols.";

    public static final String COMMENT_SIZE_CONSTRAINT_MESSAGE = "The comment must be between 1 and 1000 symbols.";

    public static final String TAG_NAME_CONSTRAINT_MESSAGE = "Tag name cannot be empty.";

    public static final String UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE = "You are not authorized to perform this action.";
    public static final String DUPLICATE_USER_INFORMATION_EXCEPTION_MESSAGE = "User with that username and/or email already exists.";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "An account with this email address is already registered";

    public static final String PASSWORD_LENGTH_ERROR_MESSAGE = "Password must be between 6 and 50 characters.";

    public static final String BLOCKED_USER_EXCEPTION_MESSAGE = "Blocked users cannot perform this action.";
    public static final String WRONG_POST_REPLY_EXCEPTION_MESSAGE = "Reply must be under the same post as the parent comment.";
    public static final String OWN_COMMENT_LIKE_EXCEPTION_MESSAGE = "You cannot like your own comment.";
    public static final String COMMENT_MODIFICATION_EXCEPTION_MESSAGE = "You are not allowed to modify this comment.";
}

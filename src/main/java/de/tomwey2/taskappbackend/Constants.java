package de.tomwey2.taskappbackend;

public class Constants {

    public static final String TASK_CREATED = "Created";
    public static final String TASK_OPEN = "Open";
    public static final String TASK_IN_PROGRESS = "In Progress";
    public static final String TASK_RESOLVED = "Resolved";
    public static final String TASK_CLOSED = "Closed";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String URI_LOCALHOST = "http://localhost";
    public static final String PATH_LOGIN = "/login";
    public static final String PATH_REGISTER = "/register";
    public static final String PATH_TASKS = "/api/tasks";
    public static final String PATH_USERS = "/api/users";
    public static final String PATH_REFRESHTOKEN = "/refreshtoken";

    public static final Long ACCESS_TOKEN_EXPIRED_IN_MSEC = (long) (50 * 60 * 1000);         // 50 minutes
    public static final Long REFRESH_TOKEN_EXPIRED_IN_MSEC = (long) (24 * 60 * 60 * 1000);   // 1 day
}

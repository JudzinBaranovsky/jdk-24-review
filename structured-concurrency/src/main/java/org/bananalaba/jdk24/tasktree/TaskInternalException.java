package org.bananalaba.jdk24.tasktree;

public class TaskInternalException extends RuntimeException {

    public TaskInternalException(String message) {
        super(message);
    }

    public TaskInternalException(String message, Throwable cause) {
        super(message, cause);
    }

}

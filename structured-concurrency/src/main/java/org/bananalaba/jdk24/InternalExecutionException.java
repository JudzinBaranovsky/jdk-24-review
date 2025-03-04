package org.bananalaba.jdk24;

public class InternalExecutionException extends RuntimeException {

    public InternalExecutionException(String message) {
        super(message);
    }

    public InternalExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}

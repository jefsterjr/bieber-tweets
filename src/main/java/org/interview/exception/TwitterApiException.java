package org.interview.exception;

public class TwitterApiException extends Exception {

    public TwitterApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwitterApiException(String message) {
        super(message);
    }
}

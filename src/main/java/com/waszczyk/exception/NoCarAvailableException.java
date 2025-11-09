package com.waszczyk.exception;

public class NoCarAvailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public NoCarAvailableException(String message) {
        super(message);
    }
}

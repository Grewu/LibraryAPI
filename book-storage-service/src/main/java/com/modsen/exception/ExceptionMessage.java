package com.modsen.exception;

import org.springframework.http.HttpStatus;

/**
 * The {@code ExceptionMessage} record is a simple data structure used to encapsulate information
 * about an exception that occurs in the application.
 *
 * <p>This record contains an HTTP status code and a message, providing a clear representation of
 * the error to be communicated to the client in a RESTful API context.
 *
 * @param status the HTTP status code associated with the exception
 * @param message a descriptive message about the exception
 */
public record ExceptionMessage(HttpStatus status, String message) {}

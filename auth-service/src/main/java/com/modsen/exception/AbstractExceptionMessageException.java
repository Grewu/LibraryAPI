package com.modsen.exception;

import org.springframework.http.HttpStatus;

/**
 * The {@code AbstractExceptionMessageException} class is an abstract base class for exceptions that
 * carry a message and an associated HTTP status code.
 *
 * <p>This class extends {@code RuntimeException} and provides a structured way to represent
 * exceptions that should be communicated to the client in a RESTful application. Subclasses must
 * implement the {@code getStatusCode()} method to provide the appropriate HTTP status code for the
 * exception.
 */
public abstract class AbstractExceptionMessageException extends RuntimeException {

  /**
   * Constructs a new {@code AbstractExceptionMessageException} with the specified detail message.
   *
   * @param message the detail message
   */
  public AbstractExceptionMessageException(String message) {
    super(message);
  }

  /**
   * Returns the HTTP status code associated with the exception.
   *
   * @return the HTTP status code
   */
  public abstract HttpStatus getStatusCode();

  /**
   * Returns an {@code ExceptionMessage} object containing the HTTP status code and the detail
   * message of the exception.
   *
   * @return an {@code ExceptionMessage} object with the status code and message
   */
  public ExceptionMessage getExceptionMessage() {
    return new ExceptionMessage(getStatusCode(), getMessage());
  }
}

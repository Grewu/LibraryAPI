package com.modsen.exception;

import org.springframework.http.HttpStatus;

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

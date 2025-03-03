package com.modsen.exception;

import org.springframework.http.HttpStatus;

/**
 * The {@code InvalidTokenException} class represents an exception that is thrown when an invalid
 * token is encountered in the application.
 *
 * <p>This exception extends {@code AbstractExceptionMessageException} and provides a specific
 * message indicating that the token is invalid.
 */
public class InvalidTokenException extends AbstractExceptionMessageException {

  private static final String EXCEPTION_MESSAGE = "Invalid token";

  /**
   * Constructs a new {@code InvalidTokenException} with a predefined message indicating that the
   * token is invalid.
   */
  public InvalidTokenException() {
    super(EXCEPTION_MESSAGE);
  }

  /**
   * Returns the HTTP status code associated with the exception, which is {@code
   * HttpStatus.UNAUTHORIZED}.
   *
   * @return {@code HttpStatus.UNAUTHORIZED}
   */
  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.UNAUTHORIZED;
  }
}

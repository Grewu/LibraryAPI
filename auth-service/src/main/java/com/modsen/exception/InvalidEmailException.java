package com.modsen.exception;

import org.springframework.http.HttpStatus;

/**
 * The {@code InvalidEmailException} class represents an exception that is thrown when an invalid
 * email address is encountered in the application.
 *
 * <p>This exception extends {@code AbstractExceptionMessageException} and provides a specific
 * message indicating the invalid email that triggered the exception.
 */
public class InvalidEmailException extends AbstractExceptionMessageException {

  private static final String EXCEPTION_MESSAGE = "Invalid email: %s";

  /**
   * Constructs a new {@code InvalidEmailException} with the specified invalid email address.
   *
   * @param token the invalid email address that caused the exception
   */
  public InvalidEmailException(String token) {
    super(String.format(EXCEPTION_MESSAGE, token));
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

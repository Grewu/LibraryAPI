package com.modsen.exception;

import org.springframework.http.HttpStatus;

/**
 * The {@code InvalidPasswordException} class represents an exception that is thrown when an invalid
 * password is encountered in the application.
 *
 * <p>This exception extends {@code AbstractExceptionMessageException} and provides a specific
 * message indicating the invalid password that triggered the exception.
 */
public class InvalidPasswordException extends AbstractExceptionMessageException {

  private static final String EXCEPTION_MESSAGE = "Invalid password: %s";

  /**
   * Constructs a new {@code InvalidPasswordException} with the specified invalid password.
   *
   * @param password the invalid password that caused the exception
   */
  public InvalidPasswordException(String password) {
    super(String.format(EXCEPTION_MESSAGE, password));
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

package com.modsen.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends AbstractExceptionMessageException {
  private static final String EXCEPTION_MESSAGE_WITH_FIELD = "%s with %s already exists";

  /**
   * Constructs a new {@code EntityAlreadyExistsException} for an entity of the specified class with
   * the given field value.
   *
   * @param entity the class of the entity that already exists
   * @param fieldValue the value of the field that already exists
   */
  public <T> EntityAlreadyExistsException(Class<T> entity, String fieldValue) {
    super(String.format(EXCEPTION_MESSAGE_WITH_FIELD, entity.getSimpleName(), fieldValue));
  }

  /**
   * Returns the HTTP status code associated with the exception, which is {@code
   * HttpStatus.CONFLICT}.
   *
   * @return {@code HttpStatus.CONFLICT}
   */
  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.CONFLICT;
  }
}

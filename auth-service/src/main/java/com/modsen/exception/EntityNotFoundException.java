package com.modsen.exception;

import org.springframework.http.HttpStatus;

/**
 * The {@code EntityNotFoundException} class represents an exception that is thrown when a requested
 * entity is not found in the system.
 *
 * <p>This exception extends {@code AbstractExceptionMessageException} and provides specific
 * messages indicating the entity type and the identifier or field value that could not be found.
 */
public class EntityNotFoundException extends AbstractExceptionMessageException {

  private static final String EXCEPTION_MESSAGE = "%s with ID %s was not found";
  private static final String EXCEPTION_MESSAGE_WITH_FIELD = "%s with '%s' was not found";

  /**
   * Constructs a new {@code EntityNotFoundException} for an entity of the specified class with the
   * given ID.
   *
   * @param entity the class of the entity that was not found
   * @param id the ID of the entity that was not found
   */
  public <T> EntityNotFoundException(Class<T> entity, Long id) {
    super(String.format(EXCEPTION_MESSAGE, entity.getSimpleName(), id));
  }

  /**
   * Constructs a new {@code EntityNotFoundException} for an entity of the specified class with the
   * given field value.
   *
   * @param entity the class of the entity that was not found
   * @param fieldValue the value of the field that was not found
   */
  public <T> EntityNotFoundException(Class<T> entity, String fieldValue) {
    super(String.format(EXCEPTION_MESSAGE_WITH_FIELD, entity.getSimpleName(), fieldValue));
  }

  /**
   * Returns the HTTP status code associated with the exception, which is {@code
   * HttpStatus.NOT_FOUND}.
   *
   * @return {@code HttpStatus.NOT_FOUND}
   */
  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
}

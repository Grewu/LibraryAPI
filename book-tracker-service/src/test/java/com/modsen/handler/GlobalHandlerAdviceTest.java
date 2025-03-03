package com.modsen.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.exception.InvalidEmailException;
import com.modsen.exception.InvalidPasswordException;
import com.modsen.exception.InvalidTokenException;
import com.modsen.util.MockEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class GlobalHandlerAdviceTest {

  private static final Long MOCK_ENTITY_ID = 1L;
  private static final String INVALID_EMAIL = "invalidEmail";
  private static final String INVALID_PASSWORD = "invalidPassword";
  private static final String ENTITY_NOT_FOUND_MESSAGE =
      "MockEntity with ID " + MOCK_ENTITY_ID + " was not found";
  private static final String INVALID_EMAIL_MESSAGE = "Invalid email: " + INVALID_EMAIL;
  private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
  private static final String INVALID_PASSWORD_MESSAGE = "Invalid password: " + INVALID_PASSWORD;
  private static final String ENTITY_ALREADY_EXISTS_MESSAGE =
      "MockEntity with ID " + MOCK_ENTITY_ID + " already exists";
  @MockitoSpyBean private GlobalHandlerAdvice handlerAdvice;

  @Test
  void handleEntityNotFoundException_shouldReturnNotFoundStatus() {
    var exception = new EntityNotFoundException(MockEntity.class, MOCK_ENTITY_ID);
    var response = handlerAdvice.handle(exception);

    assertThat(response)
        .hasFieldOrPropertyWithValue("statusCode", NOT_FOUND)
        .extracting("body")
        .hasFieldOrPropertyWithValue("status", NOT_FOUND)
        .hasFieldOrPropertyWithValue("message", ENTITY_NOT_FOUND_MESSAGE);
  }

  @Test
  void handleInvalidEmailException_shouldReturnUnauthorizedStatus() {
    var exception = new InvalidEmailException(INVALID_EMAIL);

    var response = handlerAdvice.handle(exception);

    assertThat(response)
        .hasFieldOrPropertyWithValue("statusCode", UNAUTHORIZED)
        .extracting("body")
        .hasFieldOrPropertyWithValue("status", UNAUTHORIZED)
        .hasFieldOrPropertyWithValue("message", INVALID_EMAIL_MESSAGE);
  }

  @Test
  void handleInvalidTokenException_shouldReturnUnauthorizedStatus() {
    var exception = new InvalidTokenException();
    var response = handlerAdvice.handle(exception);

    assertThat(response)
        .hasFieldOrPropertyWithValue("statusCode", UNAUTHORIZED)
        .extracting("body")
        .hasFieldOrPropertyWithValue("status", UNAUTHORIZED)
        .hasFieldOrPropertyWithValue("message", INVALID_TOKEN_MESSAGE);
  }

  @Test
  void handleInvalidPasswordException_shouldReturnUnauthorizedStatus() {
    var exception = new InvalidPasswordException(INVALID_PASSWORD);

    var response = handlerAdvice.handle(exception);

    assertThat(response)
        .hasFieldOrPropertyWithValue("statusCode", UNAUTHORIZED)
        .extracting("body")
        .hasFieldOrPropertyWithValue("status", UNAUTHORIZED)
        .hasFieldOrPropertyWithValue("message", INVALID_PASSWORD_MESSAGE);
  }

  @Test
  void handleEntityAlreadyExistsException_shouldReturnConflictStatus() {
    var exception = new EntityAlreadyExistsException(MockEntity.class, MOCK_ENTITY_ID);

    var response = handlerAdvice.handle(exception);

    assertThat(response)
        .hasFieldOrPropertyWithValue("statusCode", CONFLICT)
        .extracting("body")
        .hasFieldOrPropertyWithValue("status", CONFLICT)
        .hasFieldOrPropertyWithValue("message", ENTITY_ALREADY_EXISTS_MESSAGE);
  }
}

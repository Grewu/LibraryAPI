package com.modsen.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.modsen.data.RefreshTokenRequestTestData;
import com.modsen.data.TokenResponseTestData;
import com.modsen.data.UserTestData;
import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.exception.InvalidEmailException;
import com.modsen.exception.InvalidPasswordException;
import com.modsen.mapper.TokenMapper;
import com.modsen.mapper.UserMapper;
import com.modsen.model.entity.User;
import com.modsen.repository.UserRepository;
import com.modsen.service.api.TokenService;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  private static final String ERROR_MESSAGE = "User with ID -1 was not found";
  private static final String ERROR_INVALID_MESSAGE = "User with 'invalid_email' was not found";
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String REFRESH_TOKEN = "refreshToken";
  private static final String ENCODED_PASSWORD = "ENCODED_PASSWORD";
  private static final String EXCEPTION_ERROR = "ERROR";
  private static final String INVALID_PASSWORD = "INVALID";
  public static final int PAGE_SIZE = 2;
  @InjectMocks private UserServiceImpl userService;

  @Mock private UserMapper userMapper;
  @Mock private TokenMapper tokenMapper;

  @Mock private UserRepository userRepository;

  @Mock private TokenService tokenService;

  @Mock private PasswordEncoder passwordEncoder;

  @Nested
  class GetAuthorizationToken {

    @Test
    void shouldReturnAuthorizationTokensWhenCredentialsAreValid() {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      var user = UserTestData.builder().build().buildUser();
      var tokenResponse = TokenResponseTestData.builder().build().buildTokenResponse();

      when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(userRequest.password(), user.getPassword()))
          .thenReturn(Boolean.TRUE);
      when(tokenService.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);
      when(tokenService.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);
      when(tokenMapper.toTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN)).thenReturn(tokenResponse);

      // when
      var token = userService.getAuthorizationToken(userRequest);

      // then
      assertEquals(ACCESS_TOKEN, token.accessToken());
      assertEquals(REFRESH_TOKEN, token.refreshToken());
      verify(userRepository).findByEmail(userRequest.email());
      verify(passwordEncoder).matches(userRequest.password(), user.getPassword());
      verify(tokenService).generateAccessToken(user);
    }

    @Test
    void shouldReturnAuthorizationTokenByRefreshTokenRequest() {
      // given
      var refreshTokenRequest =
          RefreshTokenRequestTestData.builder().build().buildRefreshTokenRequest();
      var tokenResponse = TokenResponseTestData.builder().build().buildTokenResponse();

      when(tokenService.refreshAccessToken(refreshTokenRequest.refreshToken()))
          .thenReturn(ACCESS_TOKEN);
      when(tokenService.updateRefreshToken(refreshTokenRequest.refreshToken()))
          .thenReturn(REFRESH_TOKEN);
      when(tokenMapper.toTokenResponse(ACCESS_TOKEN, REFRESH_TOKEN)).thenReturn(tokenResponse);

      // when
      var token = userService.getAuthorizationToken(refreshTokenRequest);

      // then
      assertEquals(ACCESS_TOKEN, token.accessToken());
      assertEquals(REFRESH_TOKEN, token.refreshToken());
      verify(tokenService).refreshAccessToken(refreshTokenRequest.refreshToken());
      verify(tokenService).updateRefreshToken(refreshTokenRequest.refreshToken());
    }

    @Test
    void shouldThrowInvalidEmailExceptionWhenEmailIsNotFound() {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());

      // when & then
      assertThrows(
          InvalidEmailException.class, () -> userService.getAuthorizationToken(userRequest));
      verify(userRepository).findByEmail(userRequest.email());
      verifyNoInteractions(passwordEncoder, tokenService);
    }
  }

  @Nested
  class LoadUserByUsername {

    @Test
    void shouldReturnUserDetailsWhenUserIsFound() {
      // given
      var user = UserTestData.builder().build().buildUser();

      when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

      // when
      var userDetails = userService.loadUserByUsername(user.getEmail());

      // then
      assertEquals(user.getEmail(), userDetails.getUsername());
      verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserIsNotFound() {
      // given
      var email = UserTestData.builder().build().buildUser().getEmail();
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      // when & then
      assertThrows(EntityNotFoundException.class, () -> userService.loadUserByUsername(email));
      verify(userRepository).findByEmail(email);
    }
  }

  @Nested
  class Create {

    @Test
    void shouldReturnUserResponseWhenUserIsCreatedSuccessfully() {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      var user = UserTestData.builder().build().buildUser();
      var userResponse = UserTestData.builder().build().buildUserResponse();

      when(userMapper.toUser(userRequest)).thenReturn(user);
      when(passwordEncoder.encode(userRequest.password())).thenReturn(ENCODED_PASSWORD);
      when(userRepository.save(any(User.class))).thenReturn(user);
      when(userMapper.toUserResponse(user)).thenReturn(userResponse);

      // when
      var result = userService.create(userRequest);

      // then
      assertEquals(userResponse, result);
      verify(userMapper).toUser(userRequest);
      verify(passwordEncoder).encode(userRequest.password());
      verify(userRepository).save(user);
      verify(userMapper).toUserResponse(user);
    }

    @Test
    void shouldThrowInvalidEmailExceptionWhenDataAccessExceptionOccurs() {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      var user = UserTestData.builder().build().buildUser();

      when(userMapper.toUser(userRequest)).thenReturn(user);
      when(passwordEncoder.encode(userRequest.password())).thenReturn(ENCODED_PASSWORD);
      when(userRepository.save(any(User.class)))
          .thenThrow(new DataAccessException(EXCEPTION_ERROR) {});

      // when & then
      assertThrows(InvalidEmailException.class, () -> userService.create(userRequest));
      verify(userMapper).toUser(userRequest);
      verify(passwordEncoder).encode(userRequest.password());
      verify(userRepository).save(user);
    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenPasswordInvalid() {
      // given
      var userRequest =
          UserTestData.builder().withPassword(INVALID_PASSWORD).build().buildUserRequest();
      var user = UserTestData.builder().build().buildUser();

      // when
      when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(userRequest.password(), user.getPassword()))
          .thenReturn(Boolean.FALSE);

      // then
      assertThrows(
          InvalidPasswordException.class, () -> userService.getAuthorizationToken(userRequest));
      verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void shouldThrowEntityAlreadyExistsExceptionWhenUserAlreadyExist() {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();

      // when
      when(userRepository.existsByEmail(userRequest.email())).thenReturn(Boolean.TRUE);

      // then
      assertThrows(EntityAlreadyExistsException.class, () -> userService.create(userRequest));
      verify(tokenService, never()).generateAccessToken(any());
    }
  }

  @Nested
  class GetAll {

    @Test
    void getAllShouldReturnListOfUserResponses() {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var users = List.of(UserTestData.builder().build().buildUser());
      var expectedResponses = List.of(UserTestData.builder().build().buildUserResponse());

      var departmentPage = new PageImpl<>(users, pageable, PAGE_SIZE);

      doReturn(departmentPage).when(userRepository).findAll(pageable);

      IntStream.range(0, users.size())
          .forEach(
              i ->
                  doReturn(expectedResponses.get(i)).when(userMapper).toUserResponse(users.get(i)));

      // when
      var actualResponses = userService.getAll(pageable).getContent();

      // then
      assertEquals(expectedResponses, actualResponses);
    }
  }

  @Nested
  class GetById {
    @Test
    void getByIdShouldReturnExpectedUserResponse() {
      // given
      var user = UserTestData.builder().build().buildUser();
      var expectedResponse = UserTestData.builder().build().buildUserResponse();

      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);

      // when
      var actualResponse = userService.getById(user.getId());

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getByIdShouldThrowNotFoundException() {
      // given
      var id = -1L;
      // when
      var exception = assertThrows(EntityNotFoundException.class, () -> userService.getById(id));

      // then
      assertEquals(ERROR_MESSAGE, exception.getMessage());
    }
  }

  @Nested
  class GetByEmail {
    @Test
    void getByIdShouldReturnExpectedUserResponse() {
      // given
      var user = UserTestData.builder().build().buildUser();
      var expectedResponse = UserTestData.builder().build().buildUserResponse();

      when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);

      // when
      var actualResponse = userService.getByEmail(user.getEmail());

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getByIdShouldThrowNotFoundException() {
      // given
      var email = "invalid_email";
      // when
      var exception =
          assertThrows(EntityNotFoundException.class, () -> userService.getByEmail(email));

      // then
      assertEquals(ERROR_INVALID_MESSAGE, exception.getMessage());
    }
  }

  @Nested
  class Update {
    @Test
    void updateShouldReturnUserResponse() {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      var expectedResponse = UserTestData.builder().build().buildUserResponse();
      var user = UserTestData.builder().build().buildUser();

      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      when(userMapper.update(userRequest, user)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(user);
      when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);

      // when
      var actualResponse = userService.update(user.getId(), userRequest);

      // then
      assertEquals(expectedResponse, actualResponse);
    }
  }

  @Nested
  class Delete {
    @Test
    void deleteShouldCallDaoDeleteMethodThrowEntityNotFoundException() {
      // given
      var id = UserTestData.builder().build().buildUser().getId();
      var user = UserTestData.builder().build().buildUser();
      // when
      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      // then
      assertThatThrownBy(() -> userService.delete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteByUserById() {
      // given
      var user = UserTestData.builder().build().buildUser();
      var userResponse = UserTestData.builder().build().buildUserResponse();
      var id = user.getId();
      // when
      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      when(userMapper.toUserResponse(user)).thenReturn(userResponse);
      // then
      userService.delete(id);

      verify(userRepository).findById(id);
      verify(userRepository).deleteById(id);
    }
  }
}

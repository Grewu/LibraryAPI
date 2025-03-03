package com.modsen.service.impl;

import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.exception.InvalidEmailException;
import com.modsen.exception.InvalidPasswordException;
import com.modsen.mapper.TokenMapper;
import com.modsen.mapper.UserMapper;
import com.modsen.model.dto.request.RefreshTokenRequest;
import com.modsen.model.dto.request.UserRequest;
import com.modsen.model.dto.response.TokenResponse;
import com.modsen.model.dto.response.UserResponse;
import com.modsen.model.entity.User;
import com.modsen.repository.UserRepository;
import com.modsen.service.api.TokenService;
import com.modsen.service.api.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService interface, providing methods to manage users, including user
 * creation and authentication.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final TokenMapper tokenMapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  /**
   * Generates an authorization token for a user based on their email and password.
   *
   * @param userRequest the request containing the user's email and password
   * @return the generated authorization token
   * @throws InvalidEmailException if the provided email does not exist
   * @throws InvalidPasswordException if the provided password is incorrect
   */
  @Override
  public TokenResponse getAuthorizationToken(UserRequest userRequest) {
    return userRepository
        .findByEmail(userRequest.email())
        .map(
            u -> {
              if (passwordEncoder.matches(userRequest.password(), u.getPassword())) {
                var accessToken = tokenService.generateAccessToken(u);
                var refreshToken = tokenService.generateRefreshToken(u);
                return tokenMapper.toTokenResponse(accessToken, refreshToken);
              } else {
                throw new InvalidPasswordException(userRequest.password());
              }
            })
        .orElseThrow(() -> new InvalidEmailException(userRequest.email()));
  }

  @Override
  public TokenResponse getAuthorizationToken(RefreshTokenRequest refreshTokenRequest) {
    var accessToken = tokenService.refreshAccessToken(refreshTokenRequest.refreshToken());
    var updateRefreshToken = tokenService.updateRefreshToken(refreshTokenRequest.refreshToken());
    return tokenMapper.toTokenResponse(accessToken, updateRefreshToken);
  }

  /**
   * Loads a user by their email address.
   *
   * @param email the email address of the user
   * @return the UserDetails object for the user
   * @throws EntityNotFoundException if the user with the given email does not exist
   */
  @Override
  public UserDetails loadUserByUsername(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException(User.class, email));
  }

  /**
   * Creates a new user based on the provided UserRequest.
   *
   * @param userRequest the request containing user details for creation
   * @return the response containing the created user details
   * @throws InvalidEmailException if the email is already in use or invalid
   */
  @Override
  @Transactional
  public UserResponse create(UserRequest userRequest) {
    if (userRepository.existsByEmail(userRequest.email())) {
      throw new EntityAlreadyExistsException(User.class, userRequest.email());
    }
    try {
      return Optional.of(userRequest)
          .map(userMapper::toUser)
          .map(
              u -> {
                u.setPassword(passwordEncoder.encode(userRequest.password()));
                return u;
              })
          .map(userRepository::save)
          .map(userMapper::toUserResponse)
          .orElseThrow();
    } catch (DataAccessException e) {
      throw new InvalidEmailException(userRequest.email());
    }
  }

  @Override
  public UserResponse getByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .map(userMapper::toUserResponse)
        .orElseThrow(() -> new EntityNotFoundException(User.class, email));
  }

  @Override
  public Page<UserResponse> getAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(userMapper::toUserResponse);
  }

  @Override
  public UserResponse getById(Long id) {
    return userRepository
        .findById(id)
        .map(userMapper::toUserResponse)
        .orElseThrow(() -> new EntityNotFoundException(User.class, id));
  }

  @Override
  public UserResponse update(Long id, UserRequest userRequest) {
    return userRepository
        .findById(id)
        .map(current -> userMapper.update(userRequest, current))
        .map(userRepository::save)
        .map(userMapper::toUserResponse)
        .orElseThrow(() -> new EntityNotFoundException(User.class, id));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    var user =
        userRepository
            .findById(id)
            .map(userMapper::toUserResponse)
            .orElseThrow(() -> new EntityNotFoundException(User.class, id));
    userRepository.deleteById(user.id());
  }
}

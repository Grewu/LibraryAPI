package com.modsen.service.api;

import com.modsen.model.dto.request.RefreshTokenRequest;
import com.modsen.model.dto.request.UserRequest;
import com.modsen.model.dto.response.TokenResponse;
import com.modsen.model.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for managing user-related operations.
 *
 * <p>This interface extends {@link UserDetailsService} to provide user authentication and
 * management functionalities. It includes methods for generating authorization tokens and creating
 * new users.
 */
public interface UserService
    extends UserDetailsService, AbstractService<Long, UserRequest, UserResponse> {

  /**
   * Generates an authorization token for the specified user request.
   *
   * @param userRequest the request containing user details for authorization
   * @return a string representing the authorization token
   */
  TokenResponse getAuthorizationToken(UserRequest userRequest);

  TokenResponse getAuthorizationToken(RefreshTokenRequest refreshTokenRequest);

  /**
   * Creates a new user based on the provided user request.
   *
   * @param userRequest the request containing the details of the user to be created
   * @return a {@link UserResponse} containing the details of the created user
   */
  UserResponse create(UserRequest userRequest);

  UserResponse getByEmail(String email);
}

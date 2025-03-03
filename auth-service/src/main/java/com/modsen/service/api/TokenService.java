package com.modsen.service.api;

import com.modsen.model.entity.User;

/**
 * Service interface for managing authentication tokens.
 *
 * <p>This interface provides methods for generating and parsing tokens used for user authentication
 * and authorization in the application.
 */
public interface TokenService {

  /**
   * Generates a token for the specified user.
   *
   * @param user the user for whom the token is to be generated
   * @return a string representation of the generated token
   */
  String generateAccessToken(User user);

  /**
   * Generates a token for the specified user.
   *
   * @param user the user for whom the token is to be generated
   * @return a string representation of the generated token
   */
  String generateRefreshToken(User user);

  String refreshAccessToken(String refreshToken);

  /**
   * Retrieves the email address associated with the given token.
   *
   * @param token the token from which the email is to be extracted
   * @return the email address associated with the token
   */
  String getEmail(String token);

  String updateRefreshToken(String refreshToken);
}

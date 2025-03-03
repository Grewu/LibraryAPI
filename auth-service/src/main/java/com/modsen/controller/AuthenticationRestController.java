package com.modsen.controller;

import com.modsen.model.dto.request.RefreshTokenRequest;
import com.modsen.model.dto.request.UserRequest;
import com.modsen.model.dto.response.TokenResponse;
import com.modsen.model.dto.response.UserResponse;
import com.modsen.service.api.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = AuthenticationRestController.USER_API_PATH)
public class AuthenticationRestController {

  protected static final String USER_API_PATH = "/api/v0/auth";
  private final UserService userService;

  /**
   * Registers a new user in the system and returns an authorization token.
   *
   * @param userRequest the {@link UserRequest} containing user registration details.
   * @return a {@link ResponseEntity} containing the authorization token as a string with HTTP
   *     status 201 (Created) and JSON media type.
   */
  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(@Valid @RequestBody UserRequest userRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.getAuthorizationToken(userRequest));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<TokenResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.getAuthorizationToken(refreshTokenRequest));
  }

  /**
   * Authenticates a user and returns their details if authentication is successful.
   *
   * @param userRequest the {@link UserRequest} containing user authentication details.
   * @return a {@link ResponseEntity} containing the {@link UserResponse} with user details and HTTP
   *     status 201 (Created) and JSON media type.
   */
  @PostMapping("/authenticate")
  public ResponseEntity<UserResponse> authenticate(@RequestBody UserRequest userRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.create(userRequest));
  }
}

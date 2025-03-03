package com.modsen.controller;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.data.RefreshTokenRequestTestData;
import com.modsen.data.TokenResponseTestData;
import com.modsen.data.UserTestData;
import com.modsen.service.api.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerTest {
  private static final String URL_REGISTER = "/api/v0/auth/register";
  private static final String URL_AUTHENTICATE = "/api/v0/auth/authenticate";
  private static final String URL_REFRESH_TOKEN = "/api/v0/auth/refresh-token";
  @Autowired private MockMvc mockMvc;
  @MockitoBean private UserService userService;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void registerShouldReturnAuthorizationToken() throws Exception {
    // given
    var userRequest = UserTestData.builder().build().buildUserRequest();
    var tokenResponse = TokenResponseTestData.builder().build().buildTokenResponse();
    doReturn(tokenResponse).when(userService).getAuthorizationToken(userRequest);

    var requestBuilder =
        post(URL_REGISTER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRequest));

    // when
    mockMvc
        .perform(requestBuilder)
        // then
        .andExpectAll(
            status().isCreated(),
            content().contentType(MediaType.APPLICATION_JSON),
            content().json(objectMapper.writeValueAsString(tokenResponse)));
  }

  @Test
  void refreshTokenShouldReturnTokenResponse() throws Exception {
    var refreshTokenRequest =
        RefreshTokenRequestTestData.builder().build().buildRefreshTokenRequest();
    var tokenResponse = TokenResponseTestData.builder().build().buildTokenResponse();
    doReturn(tokenResponse).when(userService).getAuthorizationToken(refreshTokenRequest);

    var requestBuilder =
        post(URL_REFRESH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshTokenRequest));

    // when
    mockMvc
        .perform(requestBuilder)
        // then
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            content().json(objectMapper.writeValueAsString(tokenResponse)));
  }

  @Test
  void authenticateShouldReturnUserResponse() throws Exception {
    // given
    var userRequest = UserTestData.builder().build().buildUserRequest();
    var expectedResponse = UserTestData.builder().build().buildUserResponse();

    doReturn(expectedResponse).when(userService).create(userRequest);

    var requestBuilder =
        post(URL_AUTHENTICATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRequest));

    // when
    mockMvc
        .perform(requestBuilder)
        // then
        .andExpectAll(
            status().isCreated(),
            content().contentType(MediaType.APPLICATION_JSON),
            content().json(objectMapper.writeValueAsString(expectedResponse)));
  }
}

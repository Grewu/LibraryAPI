package com.modsen.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.data.UserTestData;
import com.modsen.service.api.UserService;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRestControllerControllerTest {
  private static final String URL = "/api/v0/users";
  private static final String URL_GET_BY_ID = URL + "/{id}";
  public static final int PAGE_SIZE = 2;
  @Autowired private MockMvc mockMvc;
  @MockitoBean private UserService userService;
  @Autowired private ObjectMapper objectMapper;

  @Nested
  class Create {
    @Test
    @WithMockUser(authorities = {"user:write"})
    void createShouldReturnUserResponse() throws Exception {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      var expectedResponse = UserTestData.builder().build().buildUserResponse();

      doReturn(expectedResponse).when(userService).create(userRequest);

      var requestBuilder =
          post(URL)
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

      verify(userService).create(any());
    }

    @Test
    void createShouldReturnForbidden() throws Exception {
      // given
      var userRequest = UserTestData.builder().build().buildUserRequest();
      var requestBuilder =
          post(URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(userRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpect(status().isForbidden());

      verify(userService, never()).create(any());
    }
  }

  @Nested
  class GetAll {

    @Test
    @WithMockUser(authorities = {"user:read"})
    void getAllShouldReturnListOfUserResponses() throws Exception {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var expectedResponses =
          List.of(
              UserTestData.builder().build().buildUserResponse(),
              UserTestData.builder().withId(2L).build().buildUserResponse());

      var expectedPage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());

      when(userService.getAll(any(Pageable.class))).thenReturn(expectedPage);

      // when
      mockMvc
          .perform(get(URL).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedPage)));
    }

    @Test
    void getAllShouldReturnForbidden() throws Exception {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      // when
      mockMvc
          .perform(get(URL).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(userService, never()).getAll(pageable);
    }
  }

  @Nested
  class GetByID {
    @Test
    @WithMockUser(authorities = {"user:read"})
    void getByIdShouldReturnUserResponse() throws Exception {
      // given
      var userProfileResponse = UserTestData.builder().build().buildUserResponse();
      var userProfileId = userProfileResponse.id();

      doReturn(userProfileResponse).when(userService).getById(userProfileId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ID, userProfileId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(userProfileResponse)));
      verify(userService).getById(any());
    }

    @Test
    void getByIdShouldReturnForbidden() throws Exception {
      // given
      var userProfileResponse = UserTestData.builder().build().buildUserResponse();
      var userProfileId = userProfileResponse.id();

      doReturn(userProfileResponse).when(userService).getById(userProfileId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ID, userProfileId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(userService, never()).getById(any());
    }
  }

  @Nested
  class Update {
    @Test
    @WithMockUser(authorities = {"user:write"})
    void updateShouldReturnUpdatedUserResponse() throws Exception {
      // given
      var userProfileId = 1L;
      var userRequest = UserTestData.builder().build().buildUserRequest();

      var updatedResponse = UserTestData.builder().build().buildUserResponse();

      doReturn(updatedResponse).when(userService).update(userProfileId, userRequest);

      var requestBuilder =
          put(URL_GET_BY_ID, userProfileId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(userRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(updatedResponse)));

      verify(userService).update(any(), any());
    }

    @Test
    void updateShouldReturnForbidden() throws Exception {
      // given
      var userId = 1L;

      // when
      mockMvc
          .perform(put(URL_GET_BY_ID, userId))
          // then
          .andExpect(status().isForbidden());

      verify(userService, never()).update(any(), any());
    }
  }

  @Nested
  class Delete {
    @Test
    @WithMockUser(authorities = {"user:delete"})
    void deleteShouldReturnNoContent() throws Exception {
      // given
      var userId = 1L;

      // when
      mockMvc
          .perform(delete(URL_GET_BY_ID, userId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isNoContent());

      verify(userService).delete(userId);
    }

    @Test
    void deleteShouldReturnForbidden() throws Exception {
      // given
      var userId = 1L;

      // when
      mockMvc
          .perform(delete(URL_GET_BY_ID, userId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(userService, never()).delete(userId);
    }
  }
}

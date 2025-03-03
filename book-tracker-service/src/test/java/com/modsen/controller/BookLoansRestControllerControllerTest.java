package com.modsen.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.data.BookLoansTestData;
import com.modsen.data.BookResponseTestData;
import com.modsen.model.dto.request.BookLoansRequest;
import com.modsen.model.entity.enums.BookStatus;
import com.modsen.service.api.BookLoansService;
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
public class BookLoansRestControllerControllerTest {
  private static final String URL = "/api/v0/books/loans";
  private static final String URL_GET_BY_ID = URL + "/{id}";
  private static final String URL_UPDATE_STATUS = URL + "/{id}/status";
  private static final String URL_GET_ALL_AVAILABLE_BOOKS = URL + "/book";
  public static final int PAGE_SIZE = 2;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private BookLoansService bookLoansService;
  @Autowired private ObjectMapper objectMapper;

  @Nested
  class Create {
    @Test
    @WithMockUser(authorities = {"user:create"})
    void createShouldReturnBookLoansResponse() throws Exception {
      // given
      var bookRequest = BookLoansTestData.builder().build().buildBookLoansRequest();
      var expectedResponse = BookLoansTestData.builder().build().buildBookLoansResponse();

      doReturn(expectedResponse).when(bookLoansService).create(bookRequest);

      var requestBuilder =
          post(URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(bookRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpectAll(
              status().isCreated(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(expectedResponse)));

      verify(bookLoansService).create(any());
    }

    @Test
    void createShouldReturnForbidden() throws Exception {
      // given
      var bookRequest = BookLoansTestData.builder().build().buildBookLoansRequest();
      var requestBuilder =
          post(URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(bookRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpect(status().isForbidden());

      verify(bookLoansService, never()).create(any());
    }
  }

  @Nested
  class GetByID {
    @Test
    @WithMockUser(authorities = {"user:read"})
    void getByIdShouldReturnBookLoansResponse() throws Exception {
      // given
      var bookLoansResponse = BookLoansTestData.builder().build().buildBookLoansResponse();
      var bookLoanId = bookLoansResponse.id();

      doReturn(bookLoansResponse).when(bookLoansService).getById(bookLoanId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ID, bookLoanId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(bookLoansResponse)));
      verify(bookLoansService).getById(any());
    }

    @Test
    void getByIdShouldReturnForbidden() throws Exception {
      // given
      var bookLoansResponse = BookLoansTestData.builder().build().buildBookLoansResponse();
      var bookLoanId = bookLoansResponse.id();

      doReturn(bookLoansResponse).when(bookLoansService).getById(bookLoanId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ID, bookLoanId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(bookLoansService, never()).getById(any());
    }
  }

  @Nested
  class GetAll {

    @Test
    @WithMockUser(authorities = {"user:read"})
    void getAllShouldReturnListOfBookLoansResponses() throws Exception {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var expectedResponses =
          List.of(
              BookLoansTestData.builder().build().buildBookLoansResponse(),
              BookLoansTestData.builder().withId(2L).build().buildBookLoansResponse());

      var expectedPage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());

      when(bookLoansService.getAll(any(Pageable.class))).thenReturn(expectedPage);

      // when
      mockMvc
          .perform(get(URL).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedPage)));
    }

    @Test
    @WithMockUser(authorities = {"user:read"})
    void getAllAvailableBookShouldReturnPageOfBookResponses() throws Exception {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var expectedResponses =
          List.of(
              BookResponseTestData.builder().build().buildBookResponse(),
              BookResponseTestData.builder().withId(2L).build().buildBookResponse());

      var expectedPage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());

      when(bookLoansService.getAllAvailableBook(any(Pageable.class))).thenReturn(expectedPage);

      // when
      mockMvc
          .perform(get(URL_GET_ALL_AVAILABLE_BOOKS).contentType(MediaType.APPLICATION_JSON))
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

      verify(bookLoansService, never()).getAll(pageable);
    }
  }

  @Nested
  class Update {
    @Test
    @WithMockUser(authorities = {"user:create"})
    void updateShouldReturnUpdatedBookLoansResponse() throws Exception {
      // given
      var bookLoanId = 1L;
      var bookLoansRequest = BookLoansTestData.builder().build().buildBookLoansRequest();

      var updatedResponse = BookLoansTestData.builder().build().buildBookLoansResponse();

      doReturn(updatedResponse).when(bookLoansService).update(bookLoanId, bookLoansRequest);

      var requestBuilder =
          put(URL_GET_BY_ID, bookLoanId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(bookLoansRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(updatedResponse)));

      verify(bookLoansService).update(eq(bookLoanId), any(BookLoansRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"user:create"})
    void updateStatusShouldReturnUpdatedBookLoansResponse() throws Exception {
      // given
      var bookLoanId = 1L;
      var status = BookLoansTestData.builder().build().buildBookLoansRequest().status();

      var updatedResponse = BookLoansTestData.builder().build().buildBookLoansResponse();

      doReturn(updatedResponse).when(bookLoansService).update(bookLoanId, status);

      var requestBuilder =
          patch(URL_UPDATE_STATUS, bookLoanId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(status));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(updatedResponse)));

      verify(bookLoansService).update(eq(bookLoanId), any(BookStatus.class));
    }

    @Test
    void updateShouldReturnForbidden() throws Exception {
      // given
      var bookLoanId = 1L;

      // when
      mockMvc
          .perform(put(URL_GET_BY_ID, bookLoanId))
          // then
          .andExpect(status().isForbidden());

      verify(bookLoansService, never()).update(eq(bookLoanId), any(BookLoansRequest.class));
    }
  }

  @Nested
  class Delete {
    @Test
    @WithMockUser(authorities = {"user:delete"})
    void deleteShouldReturnNoContent() throws Exception {
      // given
      var bookLoanId = 1L;

      // when
      mockMvc
          .perform(delete(URL_GET_BY_ID, bookLoanId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isNoContent());

      verify(bookLoansService).delete(bookLoanId);
    }

    @Test
    void deleteShouldReturnForbidden() throws Exception {
      // given
      var bookLoanId = 1L;

      // when
      mockMvc
          .perform(delete(URL_GET_BY_ID, bookLoanId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(bookLoansService, never()).delete(bookLoanId);
    }
  }
}

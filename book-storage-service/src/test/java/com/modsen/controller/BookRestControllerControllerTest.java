package com.modsen.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.data.BookTestData;
import com.modsen.service.api.BookService;
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
public class BookRestControllerControllerTest {
  private static final String URL = "/api/v0/books";
  private static final String URL_GET_BY_ID = URL + "/{id}";
  private static final String URL_GET_BY_IDS = URL + "/ids";
  private static final String URL_GET_BY_ISBN = URL + "/isbn/{isbn}";
  public static final int PAGE_SIZE = 2;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private BookService bookService;
  @Autowired private ObjectMapper objectMapper;

  @Nested
  class Create {
    @Test
    @WithMockUser(authorities = {"book:create"})
    void createShouldReturnBookResponse() throws Exception {
      // given
      var bookRequest = BookTestData.builder().build().buildBookRequest();
      var expectedResponse = BookTestData.builder().build().buildBookResponse();

      doReturn(expectedResponse).when(bookService).create(bookRequest);

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

      verify(bookService).create(any());
    }

    @Test
    void createShouldReturnForbidden() throws Exception {
      // given
      var bookRequest = BookTestData.builder().build().buildBookRequest();
      var requestBuilder =
          post(URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(bookRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpect(status().isForbidden());

      verify(bookService, never()).create(any());
    }
  }

  @Nested
  class GetAll {

    @Test
    @WithMockUser(authorities = {"book:read"})
    void getAllShouldReturnListOfUserProfileResponses() throws Exception {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var expectedResponses =
          List.of(
              BookTestData.builder().build().buildBookResponse(),
              BookTestData.builder().withId(2L).build().buildBookResponse());

      var expectedPage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());

      when(bookService.getAll(any(Pageable.class))).thenReturn(expectedPage);

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

      verify(bookService, never()).getAll(pageable);
    }
  }

  @Nested
  class GetByID {
    @Test
    @WithMockUser(authorities = {"book:read"})
    void getByIdShouldReturnUserProfileResponse() throws Exception {
      // given
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var bookId = bookResponse.id();

      doReturn(bookResponse).when(bookService).getById(bookId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ID, bookId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(bookResponse)));
      verify(bookService).getById(any());
    }

    @Test
    @WithMockUser(authorities = {"book:read"})
    void getBooksByIdsShouldReturnUserProfileResponse() throws Exception {
      // given
      final String BOOK_IDS = "bookIds";
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var listId = List.of(1L, 2L);
      var expectedResponses =
          List.of(
              BookTestData.builder().build().buildBookResponse(),
              BookTestData.builder().withId(2L).build().buildBookResponse());

      var expectedPage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());

      doReturn(expectedPage).when(bookService).getBooksByIds(anyList(), any(Pageable.class));
      when(bookService.getBooksByIds(listId, pageable)).thenReturn(expectedPage);

      // when
      mockMvc
          .perform(
              get(URL_GET_BY_IDS)
                  .param(BOOK_IDS, listId.getFirst().toString(), listId.getLast().toString())
                  .contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(content().json(objectMapper.writeValueAsString(expectedPage)));
    }

    @Test
    void getByIdShouldReturnForbidden() throws Exception {
      // given
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var bookId = bookResponse.id();

      doReturn(bookResponse).when(bookService).getById(bookId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ID, bookId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(bookService, never()).getById(any());
    }
  }

  @Nested
  class GetByIsbn {
    @Test
    @WithMockUser(authorities = {"book:read"})
    void getByIsbnShouldReturnUserProfileResponse() throws Exception {
      // given
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var bookId = bookResponse.isbn();

      doReturn(bookResponse).when(bookService).getByIsbn(bookId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ISBN, bookId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(bookResponse)));
      verify(bookService).getByIsbn(any());
    }

    @Test
    void getByIdShouldReturnForbidden() throws Exception {
      // given
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var bookId = bookResponse.isbn();

      doReturn(bookResponse).when(bookService).getByIsbn(bookId);

      // when
      mockMvc
          .perform(get(URL_GET_BY_ISBN, bookId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(bookService, never()).getById(any());
    }
  }

  @Nested
  class Update {
    @Test
    @WithMockUser(authorities = {"book:create"})
    void updateShouldReturnUpdatedUserProfileResponse() throws Exception {
      // given
      var bookId = 1L;
      var bookRequest = BookTestData.builder().build().buildBookRequest();

      var updatedResponse = BookTestData.builder().build().buildBookResponse();

      doReturn(updatedResponse).when(bookService).update(bookId, bookRequest);

      var requestBuilder =
          put(URL_GET_BY_ID, bookId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(bookRequest));

      // when
      mockMvc
          .perform(requestBuilder)
          // then
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON),
              content().json(objectMapper.writeValueAsString(updatedResponse)));

      verify(bookService).update(any(), any());
    }

    @Test
    void updateShouldReturnForbidden() throws Exception {
      // given
      var bookId = 1L;

      // when
      mockMvc
          .perform(put(URL_GET_BY_ID, bookId))
          // then
          .andExpect(status().isForbidden());

      verify(bookService, never()).update(any(), any());
    }
  }

  @Nested
  class Delete {
    @Test
    @WithMockUser(authorities = {"book:delete"})
    void deleteShouldReturnNoContent() throws Exception {
      // given
      var bookId = 1L;

      // when
      mockMvc
          .perform(delete(URL_GET_BY_ID, bookId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isNoContent());

      verify(bookService).delete(bookId);
    }

    @Test
    void deleteShouldReturnForbidden() throws Exception {
      // given
      var bookId = 1L;

      // when
      mockMvc
          .perform(delete(URL_GET_BY_ID, bookId).contentType(MediaType.APPLICATION_JSON))
          // then
          .andExpect(status().isForbidden());

      verify(bookService, never()).delete(bookId);
    }
  }
}

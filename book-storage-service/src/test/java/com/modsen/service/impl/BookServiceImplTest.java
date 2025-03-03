package com.modsen.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modsen.data.BookTestData;
import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.mapper.BookMapper;
import com.modsen.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
  private static final String ERROR_MESSAGE = "Book with ID %s was not found";
  private static final String ERROR_MESSAGE_GET_BY_ISBN = "Book with 'isbn' was not found";
  private static final String ERROR_MESSAGE_ENTITY_EXIST = "Book with 'name' already exists";
  public static final int PAGE_SIZE = 2;
  public static final String BOOK_CREATED_TOPIC = "book-created";

  @InjectMocks private BookServiceImpl bookService;

  @Mock private BookMapper bookMapper;
  @Mock private BookRepository bookRepository;
  @Mock private KafkaTemplate<String, String> kafkaTemplate;
  @Mock private CompletableFuture<SendResult<String, String>> future;

  @Nested
  class Create {

    @Test
    void shouldReturnUserResponseWhenUserIsCreatedSuccessfully() {
      // given
      var bookRequest = BookTestData.builder().build().buildBookRequest();
      var book = BookTestData.builder().build().buildBook();
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var bookId = book.getId().toString();

      when(bookRepository.existsByIsbn(bookRequest.isbn())).thenReturn(Boolean.FALSE);
      when(bookMapper.toBook(bookRequest)).thenReturn(book);
      when(bookRepository.save(book)).thenReturn(book);
      when(kafkaTemplate.send(BOOK_CREATED_TOPIC, bookId)).thenReturn(future);
      when(bookMapper.toBookResponse(book)).thenReturn(bookResponse);

      // when
      var result = bookService.create(bookRequest);

      // then
      assertEquals(bookResponse, result);
      verify(bookMapper).toBook(bookRequest);
      verify(bookRepository).save(book);
      verify(bookMapper).toBookResponse(book);
      verify(kafkaTemplate).send(BOOK_CREATED_TOPIC, bookId);
    }

    @Test
    void shouldThrowEntityAlreadyExistsException() {
      // given
      var bookRequest = BookTestData.builder().build().buildBookRequest();
      when(bookRepository.existsByIsbn(bookRequest.isbn())).thenReturn(Boolean.TRUE);
      // when & then
      assertThrows(EntityAlreadyExistsException.class, () -> bookService.create(bookRequest));
      verify(bookRepository).existsByIsbn(bookRequest.isbn());
    }
  }

  @Nested
  class GetAll {
    @Test
    void getAllShouldReturnListOfDepartmentResponses() {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var books = List.of(BookTestData.builder().build().buildBook());
      var expectedResponses = List.of(BookTestData.builder().build().buildBookResponse());

      var bookPage = new PageImpl<>(books, pageable, PAGE_SIZE);

      doReturn(bookPage).when(bookRepository).findAll(pageable);

      IntStream.range(0, books.size())
          .forEach(
              i ->
                  doReturn(expectedResponses.get(i)).when(bookMapper).toBookResponse(books.get(i)));

      // when
      var actualResponses = bookService.getAll(pageable).getContent();

      // then
      assertEquals(expectedResponses, actualResponses);
    }
  }

  @Nested
  class GetById {
    @Test
    void getByIdShouldReturnExpectedDepartmentResponse() {
      // given
      var book = BookTestData.builder().build().buildBook();
      var expectedResponse = BookTestData.builder().build().buildBookResponse();

      when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
      when(bookMapper.toBookResponse(book)).thenReturn(expectedResponse);

      // when
      var actualResponse = bookService.getById(book.getId());

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBooksByIdsShouldReturnExpectedDepartmentResponse() {
      // given
      var booksId = List.of(1L, 2L);
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var books = List.of(BookTestData.builder().build().buildBook());
      var book = BookTestData.builder().build().buildBook();
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var expectedResponses = new PageImpl<>(List.of(bookResponse), pageable, PAGE_SIZE);

      var bookPage = new PageImpl<>(books, pageable, PAGE_SIZE);
      when(bookRepository.findBooksByIdIn(booksId, pageable)).thenReturn(bookPage);
      when(bookMapper.toBookResponse(book)).thenReturn(bookResponse);

      // when
      var actualResponse = bookService.getBooksByIds(booksId, pageable);

      // then
      assertEquals(expectedResponses.getContent(), actualResponse.getContent());
      assertEquals(expectedResponses.getTotalElements(), actualResponse.getTotalElements());
      assertEquals(expectedResponses.getPageable(), actualResponse.getPageable());
    }

    @Test
    void getByIdShouldThrowNotFoundException() {
      // given
      var id = -1L;
      // when
      var exception = assertThrows(EntityNotFoundException.class, () -> bookService.getById(id));

      // then
      assertEquals(ERROR_MESSAGE.formatted(id), exception.getMessage());
    }
  }

  @Nested
  class GetByIsbn {
    @Test
    void getByIsbnShouldReturnExpectedDepartmentResponse() {
      // given

      var book = BookTestData.builder().build().buildBook();
      var isbn = book.getIsbn();
      var expectedResponse = BookTestData.builder().build().buildBookResponse();

      when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
      when(bookMapper.toBookResponse(book)).thenReturn(expectedResponse);

      // when
      var actualResponse = bookService.getByIsbn(isbn);

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getByIdShouldThrowNotFoundException() {
      // given
      var id = BookTestData.builder().build().buildBook().getIsbn();
      // when
      var exception = assertThrows(EntityNotFoundException.class, () -> bookService.getByIsbn(id));

      // then
      assertEquals(ERROR_MESSAGE_GET_BY_ISBN, exception.getMessage());
    }
  }

  @Nested
  class Update {
    @Test
    void updateShouldReturnBookResponse() {
      // given
      var bookRequest = BookTestData.builder().build().buildBookRequest();
      var expectedResponse = BookTestData.builder().build().buildBookResponse();
      var book = BookTestData.builder().build().buildBook();

      when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
      when(bookMapper.update(bookRequest, book)).thenReturn(book);
      when(bookRepository.save(book)).thenReturn(book);
      when(bookMapper.toBookResponse(book)).thenReturn(expectedResponse);

      // when
      var actualResponse = bookService.update(book.getId(), bookRequest);

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateShouldThrowEntityAlreadyExistsException() {
      // given
      var id = BookTestData.builder().build().buildBook().getId();
      var bookRequest = BookTestData.builder().build().buildBookRequest();
      // when
      when(bookRepository.existsByIsbn(bookRequest.isbn())).thenReturn(Boolean.TRUE);

      var exception =
          assertThrows(
              EntityAlreadyExistsException.class, () -> bookService.update(id, bookRequest));

      // then
      assertEquals(ERROR_MESSAGE_ENTITY_EXIST, exception.getMessage());
    }
  }

  @Nested
  class Delete {
    @Test
    void deleteShouldCallDaoDeleteMethodThrowEntityNotFoundException() {
      // given
      var id = BookTestData.builder().build().buildBook().getId();
      var book = BookTestData.builder().build().buildBook();
      // when
      when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
      // then
      assertThatThrownBy(() -> bookService.delete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteBookById() {
      // given
      var book = BookTestData.builder().build().buildBook();
      var bookResponse = BookTestData.builder().build().buildBookResponse();
      var id = book.getId();
      // when
      when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
      when(bookMapper.toBookResponse(book)).thenReturn(bookResponse);
      // then
      bookService.delete(id);

      verify(bookRepository).findById(id);
      verify(bookRepository).deleteById(id);
    }
  }
}

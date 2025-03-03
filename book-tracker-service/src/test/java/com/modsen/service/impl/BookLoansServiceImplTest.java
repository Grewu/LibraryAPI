package com.modsen.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modsen.client.BookStorageServiceClient;
import com.modsen.data.BookLoansTestData;
import com.modsen.data.BookResponseTestData;
import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.mapper.BookLoansMapper;
import com.modsen.model.entity.BookLoans;
import com.modsen.repository.BookLoansRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BookLoansServiceImplTest {
  private static final String ERROR_MESSAGE = "BookLoans with ID %s was not found";
  public static final int PAGE_SIZE = 2;

  @InjectMocks private BookLoansServiceImpl bookLoansService;

  @Mock private BookLoansMapper bookLoansMapper;
  @Mock private BookLoansRepository bookLoansRepository;
  @Mock private BookStorageServiceClient bookStorageServiceClient;

  @Nested
  class Create {

    @Test
    void shouldReturnBookLoansWhenUserIsCreatedSuccessfully() {
      // given
      var bookRequest = BookLoansTestData.builder().build().buildBookLoansRequest();
      var book = BookLoansTestData.builder().build().buildBookLoans();
      var bookResponse = BookLoansTestData.builder().build().buildBookLoansResponse();

      when(bookLoansRepository.existsByBookId(bookRequest.bookId())).thenReturn(Boolean.FALSE);
      when(bookLoansMapper.toBookLoans(bookRequest)).thenReturn(book);
      when(bookLoansRepository.save(book)).thenReturn(book);
      when(bookLoansMapper.toBookLoansResponse(book)).thenReturn(bookResponse);

      // when
      var result = bookLoansService.create(bookRequest);

      // then
      assertEquals(bookResponse, result);
      verify(bookLoansMapper).toBookLoans(bookRequest);
      verify(bookLoansRepository).save(book);
      verify(bookLoansMapper).toBookLoansResponse(book);
    }

    @Test
    void createWhenBookDoesNotExistSavesNewBookLoans() {
      // given
      var expected = BookLoansTestData.builder().build().buildBookLoans();
      var bookId = expected.getBookId();

      when(bookLoansRepository.existsByBookId(bookId)).thenReturn(Boolean.FALSE);
      when(bookLoansRepository.save(any(BookLoans.class))).thenReturn(expected);

      // when
      bookLoansService.create(String.valueOf(bookId));

      // then
      verify(bookLoansRepository).existsByBookId(bookId);
      verify(bookLoansRepository).save(any(BookLoans.class));
    }

    @Test
    void shouldThrowEntityAlreadyExistsException() {
      // given
      var bookRequest = BookLoansTestData.builder().build().buildBookLoansRequest();

      when(bookLoansRepository.existsByBookId(bookRequest.bookId())).thenReturn(Boolean.TRUE);

      // when & then
      assertThrows(EntityAlreadyExistsException.class, () -> bookLoansService.create(bookRequest));
      verify(bookLoansRepository).existsByBookId(bookRequest.bookId());
    }

    @Test
    void shouldThrowEntityAlreadyExistsExceptionInKafka() {
      // given
      var bookId = BookLoansTestData.builder().build().buildBookLoansRequest().bookId();

      when(bookLoansRepository.existsByBookId(bookId)).thenReturn(Boolean.TRUE);

      // when & then
      assertThrows(
          EntityAlreadyExistsException.class,
          () -> bookLoansService.create(String.valueOf(bookId)));
      verify(bookLoansRepository).existsByBookId(bookId);
    }
  }

  @Nested
  class GetAll {
    @Test
    void getAllShouldReturnPageOfBookLoansResponses() {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var bookLoans = List.of(BookLoansTestData.builder().build().buildBookLoans());
      var expectedResponses = List.of(BookLoansTestData.builder().build().buildBookLoansResponse());

      var bookLoansPage = new PageImpl<>(bookLoans, pageable, PAGE_SIZE);

      doReturn(bookLoansPage).when(bookLoansRepository).findAll(pageable);

      IntStream.range(0, bookLoans.size())
          .forEach(
              i ->
                  doReturn(expectedResponses.get(i))
                      .when(bookLoansMapper)
                      .toBookLoansResponse(bookLoans.get(i)));

      // when
      var actualResponses = bookLoansService.getAll(pageable).getContent();

      // then
      assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllAvailableBookShouldReturnPageOfBookResponses() {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      var expectedResponses = List.of(BookResponseTestData.builder().build().buildBookResponse());
      var listOfIds = List.of(1L, 2L);
      var bookLoansPage = new PageImpl<>(listOfIds, pageable, listOfIds.size());

      doReturn(bookLoansPage)
          .when(bookLoansRepository)
          .findAllBooksIdWhereBookStatusIsAvailable(pageable);

      var bookResponsePage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());
      doReturn(ResponseEntity.ok(bookResponsePage))
          .when(bookStorageServiceClient)
          .getBooksByIds(listOfIds, pageable);

      // when
      var actualResponses = bookLoansService.getAllAvailableBook(pageable).getContent();

      // then
      assertEquals(expectedResponses, actualResponses);
    }
  }

  @Nested
  class GetById {
    @Test
    void getByIdShouldReturnExpectedBookLoansResponse() {
      // given
      var bookLoans = BookLoansTestData.builder().build().buildBookLoans();
      var expectedResponse = BookLoansTestData.builder().build().buildBookLoansResponse();

      when(bookLoansRepository.findById(bookLoans.getId())).thenReturn(Optional.of(bookLoans));
      when(bookLoansMapper.toBookLoansResponse(bookLoans)).thenReturn(expectedResponse);

      // when
      var actualResponse = bookLoansService.getById(bookLoans.getId());

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getByIdShouldThrowNotFoundException() {
      // given
      var id = -1L;
      // when
      var exception =
          assertThrows(EntityNotFoundException.class, () -> bookLoansService.getById(id));

      // then
      assertEquals(ERROR_MESSAGE.formatted(id), exception.getMessage());
    }
  }

  @Nested
  class Update {
    @Test
    void updateShouldReturnBookLoansResponse() {
      // given
      var departmentRequest = BookLoansTestData.builder().build().buildBookLoansRequest();
      var expectedResponse = BookLoansTestData.builder().build().buildBookLoansResponse();
      var department = BookLoansTestData.builder().build().buildBookLoans();

      when(bookLoansRepository.findById(department.getId())).thenReturn(Optional.of(department));
      when(bookLoansMapper.update(departmentRequest, department)).thenReturn(department);
      when(bookLoansRepository.save(department)).thenReturn(department);
      when(bookLoansMapper.toBookLoansResponse(department)).thenReturn(expectedResponse);

      // when
      var actualResponse = bookLoansService.update(department.getId(), departmentRequest);

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateShouldReturnBookLoansResponseWithStatus() {
      // given
      var status = BookLoansTestData.builder().build().buildBookLoansRequest().status();
      var expectedResponse = BookLoansTestData.builder().build().buildBookLoansResponse();
      var bookLoans = BookLoansTestData.builder().build().buildBookLoans();

      when(bookLoansRepository.findById(bookLoans.getId())).thenReturn(Optional.of(bookLoans));
      when(bookLoansRepository.save(bookLoans)).thenReturn(bookLoans);
      when(bookLoansMapper.toBookLoansResponse(bookLoans)).thenReturn(expectedResponse);

      // when
      var actualResponse = bookLoansService.update(bookLoans.getId(), status);

      // then
      assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateShouldThrowEntityAlreadyExistsException() {
      // given
      var id = BookLoansTestData.builder().build().buildBookLoans().getId();
      var bookLoansRequest = BookLoansTestData.builder().build().buildBookLoansRequest();
      // when
      var exception =
          assertThrows(
              EntityNotFoundException.class, () -> bookLoansService.update(id, bookLoansRequest));

      // then
      assertEquals(ERROR_MESSAGE.formatted(id), exception.getMessage());
    }

    @Test
    void updateShouldReturnPageEmptyWhenFindAllBooksIdWhereBookStatusIsAvailableReturnEmpty() {
      // given
      var pageable = Pageable.ofSize(PAGE_SIZE);
      Page<Long> emptyBookIdsPage = Page.empty(pageable);

      when(bookLoansRepository.findAllBooksIdWhereBookStatusIsAvailable(pageable))
          .thenReturn(emptyBookIdsPage);

      // when
      var actual = bookLoansService.getAllAvailableBook(pageable);
      // then
      assertTrue(actual.isEmpty());
    }
  }

  @Nested
  class Delete {
    @Test
    void deleteShouldCallDaoDeleteMethodThrowEntityNotFoundException() {
      // given
      var id = BookLoansTestData.builder().build().buildBookLoans().getId();
      var department = BookLoansTestData.builder().build().buildBookLoans();
      // when
      when(bookLoansRepository.findById(department.getId())).thenReturn(Optional.of(department));
      // then
      assertThatThrownBy(() -> bookLoansService.delete(id))
          .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteUserById() {
      // given
      var user = BookLoansTestData.builder().build().buildBookLoans();
      var userResponse = BookLoansTestData.builder().build().buildBookLoansResponse();
      var id = user.getId();
      // when
      when(bookLoansRepository.findById(user.getId())).thenReturn(Optional.of(user));
      when(bookLoansMapper.toBookLoansResponse(user)).thenReturn(userResponse);
      // then
      bookLoansService.delete(id);

      verify(bookLoansRepository).findById(id);
      verify(bookLoansRepository).deleteById(id);
    }

    @Test
    void deleteShouldVerifyBookLoansWhenUserIsCreatedSuccessfully() {
      // given
      var expected = BookLoansTestData.builder().build().buildBookLoans();
      var bookId = expected.getBookId();

      when(bookLoansRepository.existsByBookId(bookId)).thenReturn(Boolean.FALSE);
      when(bookLoansRepository.save(any(BookLoans.class))).thenReturn(expected);

      // when
      bookLoansService.create(String.valueOf(bookId));

      // then
      verify(bookLoansRepository).existsByBookId(bookId);
      verify(bookLoansRepository).save(any(BookLoans.class));
    }

    @Test
    void deleteWhenBookExistSavesNewBookLoans() {
      // given
      var bookLoansResponse = BookLoansTestData.builder().build().buildBookLoansResponse();
      var bookLoans = BookLoansTestData.builder().build().buildBookLoans();
      var bookId = bookLoansResponse.bookId();

      when(bookLoansRepository.findByBookId(bookId)).thenReturn(Optional.ofNullable(bookLoans));
      when(bookLoansMapper.toBookLoansResponse(any())).thenReturn(bookLoansResponse);
      doNothing().when(bookLoansRepository).deleteByBookId(bookId);

      // when
      bookLoansService.delete(String.valueOf(bookId));

      // then
      verify(bookLoansRepository).findByBookId(bookId);
      verify(bookLoansRepository).deleteByBookId(bookId);
    }
  }
}

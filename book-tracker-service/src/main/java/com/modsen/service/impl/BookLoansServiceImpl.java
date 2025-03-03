package com.modsen.service.impl;

import static com.modsen.config.KafkaTopicConfig.BOOK_TRACKER_GROUP_ID;
import static com.modsen.config.KafkaTopicConfig.TOPIC_BOOK_CREATED;
import static com.modsen.config.KafkaTopicConfig.TOPIC_BOOK_DELETED;

import com.modsen.client.BookStorageServiceClient;
import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.mapper.BookLoansMapper;
import com.modsen.model.dto.request.BookLoansRequest;
import com.modsen.model.dto.response.BookLoansResponse;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.model.entity.BookLoans;
import com.modsen.model.entity.enums.BookStatus;
import com.modsen.repository.BookLoansRepository;
import com.modsen.service.api.BookLoansService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookLoansServiceImpl implements BookLoansService {

  private final BookLoansRepository bookLoansRepository;
  private final BookLoansMapper bookLoansMapper;
  private final BookStorageServiceClient bookStorageServiceClient;

  @Override
  @Transactional
  public BookLoansResponse create(BookLoansRequest bookLoansRequest) {
    if (bookLoansRepository.existsByBookId(bookLoansRequest.bookId())) {
      throw new EntityAlreadyExistsException(BookLoans.class, bookLoansRequest.bookId());
    }
    var bookLoans = bookLoansMapper.toBookLoans(bookLoansRequest);
    return bookLoansMapper.toBookLoansResponse(bookLoansRepository.save(bookLoans));
  }

  @Transactional
  @KafkaListener(topics = TOPIC_BOOK_CREATED, groupId = BOOK_TRACKER_GROUP_ID)
  public void create(String bookId) {
    if (bookLoansRepository.existsByBookId(Long.valueOf(bookId))) {
      throw new EntityAlreadyExistsException(BookLoans.class, bookId);
    }
    var bookLoans =
        BookLoans.builder().bookId(Long.valueOf(bookId)).status(BookStatus.AVAILABLE).build();

    bookLoansRepository.save(bookLoans);
  }

  @Override
  public Page<BookLoansResponse> getAll(Pageable pageable) {
    return bookLoansRepository.findAll(pageable).map(bookLoansMapper::toBookLoansResponse);
  }

  @Override
  public Page<BookResponse> getAllAvailableBook(Pageable pageable) {
    var bookIds = bookLoansRepository.findAllBooksIdWhereBookStatusIsAvailable(pageable);
    if (bookIds.isEmpty()) {
      return Page.empty();
    }
    var booksResponse = bookStorageServiceClient.getBooksByIds(bookIds.getContent(), pageable);
    return new PageImpl<>(
        booksResponse.getBody().getContent(), pageable, bookIds.getTotalElements());
  }

  @Override
  public BookLoansResponse getById(Long id) {
    return bookLoansRepository
        .findById(id)
        .map(bookLoansMapper::toBookLoansResponse)
        .orElseThrow(() -> new EntityNotFoundException(BookLoans.class, id));
  }

  @Override
  @Transactional
  public BookLoansResponse update(Long id, BookLoansRequest bookLoansRequest) {
    return bookLoansRepository
        .findById(id)
        .map(current -> bookLoansMapper.update(bookLoansRequest, current))
        .map(bookLoansRepository::save)
        .map(bookLoansMapper::toBookLoansResponse)
        .orElseThrow(() -> new EntityNotFoundException(BookLoans.class, id));
  }

  @Override
  @Transactional
  public BookLoansResponse update(Long id, BookStatus status) {
    var book =
        bookLoansRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(BookLoans.class, id));
    book.setStatus(status);
    var bookLoans = bookLoansRepository.save(book);
    return bookLoansMapper.toBookLoansResponse(bookLoans);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    var bookLoansResponse =
        bookLoansRepository
            .findById(id)
            .map(bookLoansMapper::toBookLoansResponse)
            .orElseThrow(() -> new EntityNotFoundException(BookLoans.class, id));
    bookLoansRepository.deleteById(bookLoansResponse.id());
  }

  @Transactional
  @KafkaListener(topics = TOPIC_BOOK_DELETED, groupId = BOOK_TRACKER_GROUP_ID)
  public void delete(String bookId) {
    var bookLoansResponse =
        bookLoansRepository
            .findByBookId(Long.valueOf(bookId))
            .map(bookLoansMapper::toBookLoansResponse)
            .orElseThrow(() -> new EntityNotFoundException(BookLoans.class, bookId));
    bookLoansRepository.deleteByBookId(bookLoansResponse.bookId());
  }
}

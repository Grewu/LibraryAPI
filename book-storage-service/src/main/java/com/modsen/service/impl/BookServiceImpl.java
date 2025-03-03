package com.modsen.service.impl;

import static com.modsen.config.KafkaTopicConfig.TOPIC_BOOK_CREATED;
import static com.modsen.config.KafkaTopicConfig.TOPIC_BOOK_DELETED;

import com.modsen.exception.EntityAlreadyExistsException;
import com.modsen.exception.EntityNotFoundException;
import com.modsen.mapper.BookMapper;
import com.modsen.model.dto.request.BookRequest;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.model.entity.Book;
import com.modsen.repository.BookRepository;
import com.modsen.service.api.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final BookMapper bookMapper;

  @Override
  @Transactional
  public BookResponse create(BookRequest bookRequest) {
    if (bookRepository.existsByIsbn(bookRequest.isbn())) {
      throw new EntityAlreadyExistsException(Book.class, bookRequest.isbn());
    }
    var bookToSave = bookRepository.save(bookMapper.toBook(bookRequest));
    kafkaTemplate.send(TOPIC_BOOK_CREATED, bookToSave.getId().toString());
    return bookMapper.toBookResponse(bookToSave);
  }

  @Override
  public Page<BookResponse> getAll(Pageable pageable) {
    return bookRepository.findAll(pageable).map(bookMapper::toBookResponse);
  }

  @Override
  public BookResponse getById(Long id) {
    return bookRepository
        .findById(id)
        .map(bookMapper::toBookResponse)
        .orElseThrow(() -> new EntityNotFoundException(Book.class, id));
  }

  @Override
  public BookResponse getByIsbn(String isbn) {
    return bookRepository
        .findByIsbn(isbn)
        .map(bookMapper::toBookResponse)
        .orElseThrow(() -> new EntityNotFoundException(Book.class, isbn));
  }

  @Override
  public Page<BookResponse> getBooksByIds(List<Long> bookIds, Pageable pageable) {
    return bookRepository.findBooksByIdIn(bookIds, pageable).map(bookMapper::toBookResponse);
  }

  @Override
  @Transactional
  public BookResponse update(Long id, BookRequest bookRequest) {
    if (bookRepository.existsByIsbn(bookRequest.isbn())) {
      throw new EntityAlreadyExistsException(Book.class, bookRequest.name());
    }
    return bookRepository
        .findById(id)
        .map(current -> bookMapper.update(bookRequest, current))
        .map(bookRepository::save)
        .map(bookMapper::toBookResponse)
        .orElseThrow(() -> new EntityNotFoundException(Book.class, id));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    var bookResponse =
        bookRepository
            .findById(id)
            .map(bookMapper::toBookResponse)
            .orElseThrow(() -> new EntityNotFoundException(Book.class, id));
    bookRepository.deleteById(bookResponse.id());
    kafkaTemplate.send(TOPIC_BOOK_DELETED, bookResponse.id().toString());
  }
}

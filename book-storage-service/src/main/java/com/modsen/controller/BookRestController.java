package com.modsen.controller;

import com.modsen.model.dto.request.BookRequest;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.service.api.BookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = BookRestController.BOOK_API_PATH)
public class BookRestController {

  protected static final String BOOK_API_PATH = "/api/v0/books";
  private final BookService bookService;

  @PostMapping
  @PreAuthorize("hasAuthority('book:create')")
  public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest bookRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookService.create(bookRequest));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('book:read')")
  public ResponseEntity<Page<BookResponse>> getAll(@PageableDefault(20) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookService.getAll(pageable));
  }

  @GetMapping("/ids")
  @PreAuthorize("hasAuthority('book:read')")
  public ResponseEntity<Page<BookResponse>> getBooksByIds(
      @RequestParam List<Long> bookIds, @PageableDefault(20) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookService.getBooksByIds(bookIds, pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('book:read')")
  public ResponseEntity<BookResponse> getById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookService.getById(id));
  }

  @GetMapping("/isbn/{isbn}")
  @PreAuthorize("hasAuthority('book:read')")
  public ResponseEntity<BookResponse> getByIsbn(@PathVariable String isbn) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookService.getByIsbn(isbn));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('book:create')")
  public ResponseEntity<BookResponse> update(
      @PathVariable Long id, @Valid @RequestBody BookRequest bookRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookService.update(id, bookRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('book:delete')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    bookService.delete(id);
    return ResponseEntity.noContent().build();
  }
}

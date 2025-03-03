package com.modsen.controller;

import com.modsen.model.dto.request.BookLoansRequest;
import com.modsen.model.dto.response.BookLoansResponse;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.model.entity.enums.BookStatus;
import com.modsen.service.api.BookLoansService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = BookLoansRestController.BOOK_LOANS_API_PATH)
public class BookLoansRestController {

  protected static final String BOOK_LOANS_API_PATH = "/api/v0/books/loans";
  private final BookLoansService bookLoansService;

  @PostMapping
  @PreAuthorize("hasAuthority('user:create')")
  public ResponseEntity<BookLoansResponse> create(
      @Valid @RequestBody BookLoansRequest bookLoansRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookLoansService.create(bookLoansRequest));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<Page<BookLoansResponse>> getAll(@PageableDefault(20) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookLoansService.getAll(pageable));
  }

  @GetMapping("/book")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<Page<BookResponse>> getAllAvailableBooks(
      @PageableDefault(20) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookLoansService.getAllAvailableBook(pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<BookLoansResponse> getById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookLoansService.getById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('user:create')")
  public ResponseEntity<BookLoansResponse> update(
      @PathVariable Long id, @Valid @RequestBody BookLoansRequest bookLoansRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookLoansService.update(id, bookLoansRequest));
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasAuthority('user:create')")
  public ResponseEntity<BookLoansResponse> updateStatus(
      @PathVariable Long id, @Valid @RequestBody BookStatus bookStatus) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(bookLoansService.update(id, bookStatus));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('user:delete')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    bookLoansService.delete(id);
    return ResponseEntity.noContent().build();
  }
}

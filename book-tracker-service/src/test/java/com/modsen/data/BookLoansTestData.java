package com.modsen.data;

import com.modsen.model.dto.request.BookLoansRequest;
import com.modsen.model.dto.response.BookLoansResponse;
import com.modsen.model.entity.BookLoans;
import com.modsen.model.entity.enums.BookStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class BookLoansTestData {

  @Builder.Default private Long id = 1L;
  @Builder.Default private Long bookId = 1L;
  @Builder.Default private BookStatus status = BookStatus.BORROWED;
  @Builder.Default private LocalDateTime createdAt = LocalDateTime.of(2025, 2, 1, 10, 0, 0, 0);
  @Builder.Default private LocalDateTime modifiedAt = LocalDateTime.of(2025, 2, 1, 10, 0, 0, 0);
  @Builder.Default private LocalDateTime returnedAt = LocalDateTime.of(2025, 2, 1, 10, 0, 0, 0);
  ;
  @Builder.Default private boolean isDeleted = false;

  public BookLoans buildBookLoans() {
    return new BookLoans(id, bookId, status, createdAt, returnedAt);
  }

  public BookLoansRequest buildBookLoansRequest() {
    return new BookLoansRequest(bookId, status);
  }

  public BookLoansResponse buildBookLoansResponse() {
    return new BookLoansResponse(id, bookId, status, createdAt, returnedAt);
  }
}

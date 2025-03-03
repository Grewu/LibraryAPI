package com.modsen.service.api;

import com.modsen.model.dto.request.BookLoansRequest;
import com.modsen.model.dto.response.BookLoansResponse;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.model.entity.enums.BookStatus;
import com.modsen.service.AbstractService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookLoansService
    extends AbstractService<Long, BookLoansRequest, BookLoansResponse> {
  BookLoansResponse update(Long id, BookStatus bookStatus);

  Page<BookResponse> getAllAvailableBook(Pageable pageable);
}

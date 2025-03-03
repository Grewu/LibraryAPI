package com.modsen.service.api;

import com.modsen.model.dto.request.BookRequest;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.service.AbstractService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService extends AbstractService<Long, BookRequest, BookResponse> {
  BookResponse getByIsbn(String isbn);

  Page<BookResponse> getBooksByIds(List<Long> bookIds, Pageable pageable);
}

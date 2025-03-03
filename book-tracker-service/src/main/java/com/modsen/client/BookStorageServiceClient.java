package com.modsen.client;

import com.modsen.model.dto.response.BookResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(
    value = "book-storage-service",
    url = "${book-storage-service.url}",
    configuration = FeignRequestInterceptor.class)
public interface BookStorageServiceClient {
  @GetMapping("/ids")
  ResponseEntity<Page<BookResponse>> getBooksByIds(
      @RequestParam List<Long> bookIds, @PageableDefault(20) Pageable pageable);
}

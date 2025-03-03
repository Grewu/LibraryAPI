package com.modsen.data;

import com.modsen.model.dto.response.BookResponse;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class BookResponseTestData {

  @Builder.Default private Long id = 1L;
  @Builder.Default private String isbn = "isbn";
  @Builder.Default private String name = "name";
  @Builder.Default private String genre = "genre";
  @Builder.Default private String description = "description";
  @Builder.Default private String author = "author";

  public BookResponse buildBookResponse() {
    return new BookResponse(id, isbn, name, genre, description, author);
  }
}

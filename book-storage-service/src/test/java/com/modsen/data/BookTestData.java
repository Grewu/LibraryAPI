package com.modsen.data;

import com.modsen.model.dto.request.BookRequest;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.model.entity.Book;
import com.modsen.model.entity.enums.GenreType;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class BookTestData {

  @Builder.Default private Long id = 1L;
  @Builder.Default private String isbn = "isbn";
  @Builder.Default private String name = "name";
  @Builder.Default private GenreType genre = GenreType.FICTION;
  @Builder.Default private String description = "description";
  @Builder.Default private String author = "author";

  public BookResponse buildBookResponse() {
    return new BookResponse(id, isbn, name, genre, description, author);
  }

  public BookRequest buildBookRequest() {
    return new BookRequest(isbn, name, genre, description, author);
  }

  public Book buildBook() {
    return new Book(id, isbn, name, genre, description, author);
  }
}

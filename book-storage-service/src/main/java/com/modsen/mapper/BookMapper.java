package com.modsen.mapper;

import com.modsen.model.dto.request.BookRequest;
import com.modsen.model.dto.response.BookResponse;
import com.modsen.model.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

  @Mapping(target = Book.Fields.id, ignore = true)
  @Mapping(target = Book.Fields.name, source = Book.Fields.name)
  @Mapping(target = Book.Fields.author, source = Book.Fields.author)
  @Mapping(target = Book.Fields.isbn, source = Book.Fields.isbn)
  Book toBook(BookRequest bookRequest);

  @Mapping(target = Book.Fields.isbn, source = Book.Fields.isbn)
  BookResponse toBookResponse(Book book);

  @Mapping(target = Book.Fields.id, ignore = true)
  @Mapping(target = Book.Fields.name, source = Book.Fields.name)
  Book update(BookRequest bookRequest, @MappingTarget Book current);
}

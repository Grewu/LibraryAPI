package com.modsen.mapper;

import com.modsen.model.dto.request.BookLoansRequest;
import com.modsen.model.dto.response.BookLoansResponse;
import com.modsen.model.entity.BookLoans;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookLoansMapper {

  @Mapping(target = BookLoans.Fields.id, ignore = true)
  @Mapping(target = BookLoans.Fields.createdAt, ignore = true)
  @Mapping(target = BookLoans.Fields.returnedAt, ignore = true)
  BookLoans toBookLoans(BookLoansRequest request);

  @Mapping(target = BookLoans.Fields.createdAt, ignore = true)
  BookLoansResponse toBookLoansResponse(BookLoans bookLoans);

  @Mapping(target = BookLoans.Fields.id, ignore = true)
  @Mapping(target = BookLoans.Fields.createdAt, ignore = true)
  @Mapping(target = BookLoans.Fields.returnedAt, ignore = true)
  BookLoans update(BookLoansRequest request, @MappingTarget BookLoans bookLoans);
}

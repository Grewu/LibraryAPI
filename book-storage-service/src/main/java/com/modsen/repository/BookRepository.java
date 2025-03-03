package com.modsen.repository;

import com.modsen.model.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends AbstractRepository<Long, Book> {
  Optional<Book> findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);

  @Query("SELECT b FROM Book b WHERE b.id IN :bookIds")
  Page<Book> findBooksByIdIn(@Param("bookIds") List<Long> bookIds, Pageable pageable);
}

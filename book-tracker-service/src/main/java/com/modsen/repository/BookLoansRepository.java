package com.modsen.repository;

import com.modsen.model.entity.BookLoans;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLoansRepository extends JpaRepository<BookLoans, Long> {

  boolean existsByBookId(Long bookId);

  Optional<BookLoans> findByBookId(Long bookId);

  void deleteByBookId(Long bookId);

  @Query("SELECT b.bookId FROM BookLoans b WHERE b.status = 'AVAILABLE'")
  Page<Long> findAllBooksIdWhereBookStatusIsAvailable(Pageable pageable);
}

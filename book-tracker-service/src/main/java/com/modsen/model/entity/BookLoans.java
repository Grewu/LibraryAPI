package com.modsen.model.entity;

import com.modsen.model.entity.enums.BookStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Builder
@SoftDelete
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_loans")
@EntityListeners(AuditingEntityListener.class)
public class BookLoans {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "book_id", nullable = false, unique = true)
  private Long bookId;

  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status", nullable = false)
  private BookStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime createdAt;

  @CreatedDate
  @Column(name = "returned_at", nullable = false)
  private LocalDateTime returnedAt;

  public static class Fields {
    public static final String id = "id";
    public static final String bookId = "bookId";
    public static final String user = "user";
    public static final String status = "status";
    public static final String createdAt = "createdAt";
    public static final String modifiedAt = "modifiedAt";
    public static final String returnedAt = "returnedAt";
    public static final String deleted = "deleted";
  }
}

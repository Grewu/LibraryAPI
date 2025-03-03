package com.modsen.model.entity;

import com.modsen.model.entity.enums.GenreType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "isbn", nullable = false, unique = true)
  private String isbn;

  @Column(name = "name", nullable = false)
  private String name;

  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "genre", nullable = false)
  private GenreType genre;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "author", nullable = false)
  private String author;

  public static class Fields {
    public static final String id = "id";
    public static final String isbn = "isbn";
    public static final String name = "name";
    public static final String genre = "genre";
    public static final String description = "description";
    public static final String author = "author";
  }
}

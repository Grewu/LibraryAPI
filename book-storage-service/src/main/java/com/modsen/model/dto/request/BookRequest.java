package com.modsen.model.dto.request;

import com.modsen.model.entity.enums.GenreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
    @NotBlank(message = "ISBN cannot be blank")
        @Size(max = 20, message = "ISBN length must not exceed 20 characters")
        String isbn,
    @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name length must not exceed 255 characters")
        String name,
    @NotNull(message = "Genre type cannot be null") GenreType genre,
    @NotBlank(message = "Description cannot be blank") String description,
    @NotBlank(message = "Author cannot be blank") String author) {}

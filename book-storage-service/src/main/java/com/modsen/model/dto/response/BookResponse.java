package com.modsen.model.dto.response;

import com.modsen.model.entity.enums.GenreType;

public record BookResponse(
    Long id, String isbn, String name, GenreType genre, String description, String author) {}

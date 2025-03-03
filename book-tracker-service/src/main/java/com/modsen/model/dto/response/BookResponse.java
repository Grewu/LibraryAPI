package com.modsen.model.dto.response;

public record BookResponse(
    Long id, String isbn, String name, String genre, String description, String author) {}

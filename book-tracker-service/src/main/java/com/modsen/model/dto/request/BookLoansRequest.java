package com.modsen.model.dto.request;

import com.modsen.model.entity.enums.BookStatus;
import jakarta.validation.constraints.NotNull;

public record BookLoansRequest(
    @NotNull(message = "Book ID cannot be null") Long bookId,
    @NotNull(message = "Status cannot be null") BookStatus status) {}

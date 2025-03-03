package com.modsen.model.dto.response;

import com.modsen.model.entity.enums.BookStatus;
import java.time.LocalDateTime;

public record BookLoansResponse(
    Long id, Long bookId, BookStatus status, LocalDateTime createdAt, LocalDateTime returnedAt) {}

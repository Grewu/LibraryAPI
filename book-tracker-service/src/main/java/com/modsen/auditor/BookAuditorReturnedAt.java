package com.modsen.auditor;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

public class BookAuditorReturnedAt implements AuditorAware<LocalDateTime> {
  @Value("${book.default.return-days}")
  private int defaultReturnDays;

  @NonNull
  @Override
  public Optional<LocalDateTime> getCurrentAuditor() {
    return Optional.of(LocalDateTime.now().plusDays(defaultReturnDays));
  }
}

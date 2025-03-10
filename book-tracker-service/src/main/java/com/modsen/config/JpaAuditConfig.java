package com.modsen.config;

import com.modsen.auditor.BookAuditorReturnedAt;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
  @Bean
  public AuditorAware<LocalDateTime> auditorAware() {
    return new BookAuditorReturnedAt();
  }
}

package com.modsen.repository;

import com.modsen.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}

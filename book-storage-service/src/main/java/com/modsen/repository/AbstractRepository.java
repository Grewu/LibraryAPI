package com.modsen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<K, T> extends JpaRepository<T, K> {}

package com.modsen.model.entity;

import com.modsen.model.entity.enums.PrivilegeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "privileges")
public class Privilege {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private PrivilegeType name;

  @ManyToMany(mappedBy = "privileges", fetch = FetchType.LAZY)
  private Set<Role> roles = new HashSet<>();
}

package com.modsen.model.entity;

import com.modsen.model.entity.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private RoleType name;

  @JoinTable(
      name = "role_privilege",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "privilege_id"))
  @ManyToMany(fetch = FetchType.LAZY)
  private Set<Privilege> privileges = new HashSet<>();

  public Role(Long role) {
    this.id = role;
  }

  public Role(Long id, RoleType name) {
    this.id = id;
    this.name = name;
  }
}

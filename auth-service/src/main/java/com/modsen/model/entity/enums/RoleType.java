package com.modsen.model.entity.enums;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum RoleType {
  ADMIN(Arrays.stream(PrivilegeType.values()).map(PrivilegeType::getGrantedAuthority).toList()),

  USER(List.of(PrivilegeType.BOOK_READ.getGrantedAuthority()));

  private final List<GrantedAuthority> authorities;

  RoleType(List<GrantedAuthority> grantedAuthorities) {
    this.authorities = grantedAuthorities;
  }
}

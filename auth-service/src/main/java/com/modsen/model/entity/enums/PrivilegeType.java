package com.modsen.model.entity.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum PrivilegeType {
  BOOK_CREATE(new SimpleGrantedAuthority("book:create")),
  BOOK_READ(new SimpleGrantedAuthority("book:read")),
  BOOK_DELETE(new SimpleGrantedAuthority("book:delete")),
  USER_READ(new SimpleGrantedAuthority("user:read")),
  USER_WRITE(new SimpleGrantedAuthority("user:write")),
  USER_DELETE(new SimpleGrantedAuthority("user:delete"));

  private final GrantedAuthority grantedAuthority;

  PrivilegeType(GrantedAuthority grantedAuthority) {
    this.grantedAuthority = grantedAuthority;
  }
}

package com.example.demo.security;

import static com.example.demo.security.UserPermission.COURSE_READ;
import static com.example.demo.security.UserPermission.STUDENT_READ;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
  STUDENT(EnumSet.of(COURSE_READ)),
  ADMIN(EnumSet.allOf(UserPermission.class)),
  LECTURER(EnumSet.of(STUDENT_READ, COURSE_READ));

  private final Set<UserPermission> permissions;

  UserRole(Set<UserPermission> permissions) {
    this.permissions = permissions;
  }

  public Set<GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = permissions.stream()
        .map(permissions -> new SimpleGrantedAuthority(permissions.getPermission()))
        .collect(Collectors.toSet());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }
}

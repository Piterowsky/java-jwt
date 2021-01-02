package com.example.demo.security;

public enum UserPermission {
  // COURSE
  COURSE_READ("course:read"),
  COURSE_WRITE("course:write"),

  // STUDENT
  STUDENT_READ("student:read"),
  STUDENT_WRITE("student:write");

  private final String permission;

  UserPermission(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }
}

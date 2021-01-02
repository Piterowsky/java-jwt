package com.example.demo.security;

import java.util.Optional;

public interface ApplicationUserDao {

  Optional<ApplicationUser> findApplicationUserByUsername(String username);

}

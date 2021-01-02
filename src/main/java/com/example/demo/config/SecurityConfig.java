package com.example.demo.config;

import static com.example.demo.security.UserPermission.COURSE_READ;
import static com.example.demo.security.UserPermission.COURSE_WRITE;
import static com.example.demo.security.UserRole.ADMIN;
import static com.example.demo.security.UserRole.LECTURER;
import static com.example.demo.security.UserRole.STUDENT;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final DataSource dataSource;

  public SecurityConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    var courseUrlPattern = "/api/course/**";
    var studentUrlPattern = "/api/student/**";
    String[] availableForAllPatterns = {"/", "index", "/css/**", "/js/**", "/h2-console/**"};

    http
        /*.csrf(csrf -> {
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            csrf.requireCsrfProtectionMatcher(
                httpServletRequest -> !httpServletRequest.getServletPath().startsWith("/api/student"));
        })*/

        .csrf().disable()
        .authorizeRequests()
        .antMatchers(availableForAllPatterns).permitAll()

        .antMatchers(studentUrlPattern).hasAnyRole(STUDENT.name(), ADMIN.name())

        .antMatchers(HttpMethod.DELETE, courseUrlPattern).hasAuthority(COURSE_WRITE.getPermission())
        .antMatchers(HttpMethod.POST, courseUrlPattern).hasAuthority(COURSE_WRITE.getPermission())
        .antMatchers(HttpMethod.PUT, courseUrlPattern).hasAuthority(COURSE_WRITE.getPermission())
        .antMatchers(HttpMethod.GET, courseUrlPattern).hasAuthority(COURSE_READ.getPermission())

        .anyRequest()
        .authenticated()
        .and()
        .headers().frameOptions().sameOrigin()
        .and()
        .formLogin();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(daoAuthenticationProvider());
  }

  @Override
  @Bean
  protected UserDetailsService userDetailsService() {
    var jdbcDao = new JdbcDaoImpl();
    jdbcDao.setDataSource(dataSource);
    return jdbcDao;
  }

  @Bean
  UserDetailsManager users(DataSource dataSource) {
    UserDetails student = User.builder()
        .username("student")
        .password(passwordEncoder().encode("student"))
        .authorities(STUDENT.getAuthorities())
        .build();
    UserDetails lecturer = User.builder()
        .username("lecturer")
        .password(passwordEncoder().encode("lecturer"))
        .authorities(LECTURER.getAuthorities())
        .build();
    UserDetails admin = User.builder()
        .username("admin")
        .password(passwordEncoder().encode("admin"))
        .authorities(ADMIN.getAuthorities())
        .build();

    JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    userDetailsManager.createUser(student);
    userDetailsManager.createUser(lecturer);
    userDetailsManager.createUser(admin);
    return userDetailsManager;
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    var provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(userDetailsService());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

}

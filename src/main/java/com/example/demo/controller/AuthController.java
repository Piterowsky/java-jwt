package com.example.demo.controller;

import com.example.demo.dto.AuthenticateRequest;
import com.example.demo.dto.AuthenticateResponse;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/auth")
    public AuthenticateResponse authenticate(@RequestBody AuthenticateRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        var userDetails = userService.loadUserByUsername(request.getUsername());
        var token = JwtUtil.generateToken(userDetails);
        return new AuthenticateResponse(token);

    }

}

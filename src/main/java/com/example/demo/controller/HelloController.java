package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping
  public String hello() {
    return "<h1 style=\"color: red; font-size: 72px\">hello world</h1>".toUpperCase();
  }

}

package com.example.demo.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/course")
public class CourseController {

  @GetMapping
  public String getCourse() {
    System.out.println("GET - COURSE");
    return "GET - COURSE";
  }

  @PostMapping
  public String postCourse() {
    System.out.println("POST - COURSE");
    return "POST - COURSE";
  }

  @PutMapping
  public String putCourse() {
    System.out.println("PUT - COURSE");
    return "PUT - COURSE";
  }

  @DeleteMapping
  public String deleteCourse() {
    System.out.println("DELETE - COURSE");
    return "DELETE - COURSE";
  }

}

package com.example.demo.controller;

import com.example.demo.model.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {

  @GetMapping("/{studentId}")
  public Student getStudent(@PathVariable Long studentId) {
    var student = new Student();
    student.setId(studentId);
    student.setName("lolo");
    return student;
  }

}

package com.EmployeeManager.Application.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    public Employee(){}
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    public Long id;
    String Fname;
    String Lname;
    String email;
    String jobTitle;
    String imageUrl;
    String mobilenumber;


}

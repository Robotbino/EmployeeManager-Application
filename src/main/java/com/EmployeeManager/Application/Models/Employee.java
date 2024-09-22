package com.EmployeeManager.Application.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
public class Employee implements Serializable {
    public Employee(){}
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false,nullable = false)
    public Long id;
    String name;
    String email;
    String jobTitle;
    String imageUrl;
    String phone;
    @Column(updatable = false,nullable = false)
    String employeeCode;

    public Employee(String name,String email,String jobTitle,String phone,String imageUrl,String employeeCode)
    {
        this.name=name;
        this.email=email;
        this.jobTitle = jobTitle;
        this.phone = phone;
        this.imageUrl= imageUrl;
        this.employeeCode= employeeCode;
    }
    @Override
    public String toString()
    {
        return "Employee{"+
                "id ="+ id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", phone='" + phone+ '\'' +
                ", imageUrl='" + imageUrl+ '\'' +
                '}';
    }
}

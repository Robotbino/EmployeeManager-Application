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

    //Getters(return) and Setters(void)
    public Long getId()
    {
        return id;
    }
    public void setLong(Long id)
    {
        this.id=id;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    //getter for all the emails
    public String getEmail(){
        return email;
    }
    //setter for all the emails
    public void setEmail(String email)
    {
        this.email=email;
    }

    public String getJobTitle()
    {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle)
    {
        this.jobTitle=jobTitle;
    }

    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }

    public String getImageUrl(){
        return imageUrl;
    }
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl=imageUrl;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
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

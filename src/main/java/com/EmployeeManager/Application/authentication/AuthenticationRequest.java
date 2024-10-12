package com.EmployeeManager.Application.authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {

    private String Email;
    String password;

    public void setEmail(){
        this.Email=Email;
    }
    public void setPassword()
    {
        this.password=password;
    }
    public String getEmail()
    {
        return Email;
    }
    public String getPassword()
    {
        return password;
    }
}

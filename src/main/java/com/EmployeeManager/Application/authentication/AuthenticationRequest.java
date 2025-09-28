package com.EmployeeManager.Application.authentication;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
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
}

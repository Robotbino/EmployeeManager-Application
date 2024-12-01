package com.EmployeeManager.Application.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class AuthenticationResponse {
    @JsonProperty("access token")
    String token;
    public void setToken(String token){
        this.token=token;
    }
}

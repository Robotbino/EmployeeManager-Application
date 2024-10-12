package com.EmployeeManager.Application.Services;

import com.EmployeeManager.Application.Models.AuthUser;
import com.EmployeeManager.Application.Models.Role;
import com.EmployeeManager.Application.Repositories.AuthRepository;
import com.EmployeeManager.Application.authentication.AuthenticationResponse;
import com.EmployeeManager.Application.authentication.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    //This is where you will find the business logic of the authentication controllers
    public AuthenticationResponse register(RegisterRequest request)
    {
        var authUser = AuthUser.builder()
                .firstName(request.getfirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = repository.save(authUser);
        var jwtToken = jwtService.generateToken(authUser);
    }


}

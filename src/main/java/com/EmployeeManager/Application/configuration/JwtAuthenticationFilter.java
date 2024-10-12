package com.EmployeeManager.Application.configuration;

import com.EmployeeManager.Application.Services.JwtService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
//This is where we are going to be building the Authentication filter and  specifying its behaviours
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void doFilterInternal(
            @NonNull
            //Our request, we can intercept data from the request and send it back in the response
            HttpServletRequest request,
            @NonNull
            //Presenting new data in the response.
            HttpServletResponse response,
            @NonNull
            FilterChain filterChain
            //The chain of responsibility design-pattern contains a list of all the other filter

    ) throws ServletException, IOException{
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

    }
}

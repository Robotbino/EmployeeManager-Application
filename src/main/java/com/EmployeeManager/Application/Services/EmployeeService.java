package com.EmployeeManager.Application.Services;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.EmployeeManager.Application.Repositories.employeeRepo;
import com.EmployeeManager.Application.authentication.AuthenticationResponse;
@Service
@AllArgsConstructor
public class EmployeeService {
    private  employeeRepo employeeRepo;

    private AuthenticationResponse authenticationResponse;

}

package com.EmployeeManager.Application.Services;
import com.EmployeeManager.Application.Models.Employee;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.EmployeeManager.Application.Repositories.employeeRepo;
import com.EmployeeManager.Application.authentication.AuthenticationResponse;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class EmployeeService {

    private  employeeRepo employeeRepo;
    private AuthenticationResponse authenticationResponse;

    public Employee addEmployee(Employee employee)
    {
        //This is where we set the Random User ID and convert it to a string
        employee.setEmployeeCode(UUID.randomUUID().toString());
        return employeeRepo.save(employee);
    };

    //Returns a list of all the employees
    public List<Employee> findAllEmployees(){ return employeeRepo.findAll();}
    //Update Employee records
    public Employee updateEmployee(Employee employee){ return employeeRepo.save(employee);}
    //Delete employee records
    public Employee findEmployeeById(Long Id) {
        return employeeRepo.findEmployeeById(id)
                .orElseThrow(() -> new UserNotFoundException("User by id" + id + "was not found"));

    }
}

package com.EmployeeManager.Application.Repositories;

import com.EmployeeManager.Application.Models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface employeeRepo extends JpaRepository<Employee, Long>{
    void deleteEmployeeById(Long id);

    Optional<Employee> findEmployeeById(Long id);
}

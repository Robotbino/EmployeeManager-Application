package com.EmployeeManager.Application.Repositories;


import com.EmployeeManager.Application.Models.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthUser,Integer> {

    Optional<AuthUser> findByEmail(String email);
}

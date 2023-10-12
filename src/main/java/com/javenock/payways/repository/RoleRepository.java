package com.javenock.payways.repository;

import com.javenock.payways.model.ERole;
import com.javenock.payways.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
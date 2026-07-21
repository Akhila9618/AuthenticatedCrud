package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.db.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Roles,Long> {

	Roles findByRoleName(String role);

}

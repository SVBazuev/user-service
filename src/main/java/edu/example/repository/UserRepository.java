package edu.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.example.core.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}

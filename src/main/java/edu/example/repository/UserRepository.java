package edu.example.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.example.core.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable(value = "users", key = "#id")
    Optional<User> findById(Long id);

    @Cacheable(value = "usersByEmail", key = "#email")
    Optional<User> findByEmail(String email);
}

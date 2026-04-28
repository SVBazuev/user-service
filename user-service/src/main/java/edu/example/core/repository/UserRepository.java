package edu.example.core.repository;


import edu.example.core.entity.User;
import java.util.List;
import java.util.Optional;


public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void update(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
}

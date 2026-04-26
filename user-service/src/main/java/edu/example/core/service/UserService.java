package edu.example.core.service;


import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.entity.User;
import edu.example.core.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;
    private final SessionFactory sessionFactory;

    public UserService(UserRepository userRepository, SessionFactory sessionFactory) {
        this.userRepository = userRepository;
        this.sessionFactory = sessionFactory;
    }

    public UserResponse create(UserRequest request) {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            User user = UserMapper.toEntity(request);
            User saved = userRepository.save(user);
            tx.commit();
            return UserMapper.toResponse(saved);
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException("Ошибка при создании пользователя", e);
        }
    }

    public UserResponse getById(Long id) {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь с id=" + id + " не найден"));
            tx.commit();
            return UserMapper.toResponse(user);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public List<UserResponse> getAll() {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            List<UserResponse> responses = userRepository.findAll().stream()
                    .map(UserMapper::toResponse)
                    .collect(Collectors.toList());
            tx.commit();
            return responses;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException("Ошибка при получении списка пользователей", e);
        }
    }

    public UserResponse update(UserRequest request) {
        return update(request.getId(), request);
    }

    public UserResponse update(Long id, UserRequest request) {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь с id=" + id + " не найден"));
            UserMapper.updateEntity(existing, request);
            userRepository.update(existing);
            tx.commit();
            return UserMapper.toResponse(existing);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public void delete(Long id) {
        Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("Пользователь с id=" + id + " не найден");
            }
            userRepository.delete(id);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
}

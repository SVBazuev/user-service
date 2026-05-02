package edu.example.core.service;


import java.util.List;
import java.util.stream.Collectors;


import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Transaction;

import edu.example.core.dto.DTO;
import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.entity.User;
import edu.example.core.repository.UserRepository;
import edu.example.exception.ValidationException;
import edu.example.exception.DataAccessException;
import edu.example.exception.DatabaseConnectionException;
import edu.example.exception.UserNotFoundException;


public class UserService {
    private static final Logger log = LoggerFactory.getLogger(
        UserService.class
    );
    private final UserRepository userRepository;
    private final SessionFactory sessionFactory;

    public UserService(UserRepository userRepository, SessionFactory sessionFactory) {
        this.userRepository = userRepository;
        this.sessionFactory = sessionFactory;
    }

    public DTO<UserResponse> create(DTO<UserRequest> dto) {
        UserRequest request = dto.getData();
        log.info("Создание пользователя: email={}", request.getEmail());
        Transaction tx = null;
        try {
            validate(request, false);
            User user = UserMapper.toEntity(request);
            tx = sessionFactory.getCurrentSession().beginTransaction();
            User saved = userRepository.save(user);
            tx.commit();
            log.info(
                "Пользователь создан: id={}, email={}",
                saved.getId(), saved.getEmail()
            );
            return DTO.success(
                UserMapper.toResponse(saved), "Пользователь создан"
            );
        } catch (ValidationException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.warn("Ошибка валидации: {}", e.getMessage());
            return DTO.error(
                e.getMessage(),
                400
            );
        } catch (DatabaseConnectionException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка подключения к БД при создании: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Сервис временно недоступен, попробуйте позже",
                503
            );
        } catch (DataAccessException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка доступа к данным при создании: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Не удалось сохранить пользователя",
                500
            );
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Непредвиденная ошибка при создании: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Внутренняя ошибка сервера",
                500
            );
        }
    }

    public DTO<UserResponse> getById(DTO<UserRequest> dto) {
        Long id = dto.getData().getId();
        log.info("Поиск пользователя по id={}", id);
        Transaction tx = null;
        try {
            tx = sessionFactory.getCurrentSession().beginTransaction();
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            tx.commit();
            return DTO.success(UserMapper.toResponse(user));
        } catch (UserNotFoundException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.warn(e.getMessage());
            return DTO.error(e.getMessage(), 404);
        } catch (DatabaseConnectionException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка подключения к БД при поиске: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Сервис временно недоступен",
                503
            );
        } catch (DataAccessException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка доступа к данным при поиске: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Не удалось получить пользователя",
                500
            );
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Непредвиденная ошибка при поиске: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Внутренняя ошибка сервера",
                500
            );
        }
    }

    public DTO<List<UserResponse>> getAll() {
        log.info("Запрос всех пользователей");
        Transaction tx = null;
        try {
            tx = sessionFactory.getCurrentSession().beginTransaction();
            List<UserResponse> users = userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
            tx.commit();
            return DTO.success(users);
        } catch (DatabaseConnectionException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка подключения к БД при получении списка: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Сервис временно недоступен",
                503
            );
        } catch (DataAccessException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка доступа к данным при получении списка: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Не удалось получить список пользователей",
                500
            );
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Непредвиденная ошибка при получении списка: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Внутренняя ошибка сервера",
                500
            );
        }
    }

    public DTO<UserResponse> update(DTO<UserRequest> dto) {
        return update(dto.getData().getId(), dto.getData());
    }

    public DTO<UserResponse> update(Long id, UserRequest request) {
        log.info("Обновление пользователя id={}", id);
        Transaction tx = null;
        try {
            validate(request, true);
            tx = sessionFactory.getCurrentSession().beginTransaction();
            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            UserMapper.updateEntity(existing, request);
            userRepository.update(existing);
            tx.commit();
            log.info("Пользователь id={} обновлён", id);
            return DTO.success(
                UserMapper.toResponse(existing),
                "Пользователь обновлён"
            );
        } catch (ValidationException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.warn("Ошибка валидации: {}", e.getMessage());
            return DTO.error(e.getMessage(), 400);
        } catch (UserNotFoundException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.warn(e.getMessage());
            return DTO.error(e.getMessage(), 404);
        } catch (DatabaseConnectionException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка подключения к БД при обновлении: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Сервис временно недоступен",
                503
            );
        } catch (DataAccessException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка доступа к данным при обновлении: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Не удалось обновить пользователя",
                500
            );
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Непредвиденная ошибка при обновлении: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Внутренняя ошибка сервера",
                500
            );
        }
    }

    public DTO<Void> delete(DTO<UserRequest> dto) {
        Long id = dto.getData().getId();
        log.info("Удаление пользователя с id={}", id);
        Transaction tx = null;
        try {
            tx = sessionFactory.getCurrentSession().beginTransaction();
            userRepository.deleteById(id);
            tx.commit();
            log.info("Пользователь id={} удалён", id);
            return DTO.success(null, "Пользователь удалён");
        } catch (UserNotFoundException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.warn(e.getMessage());
            return DTO.error(e.getMessage(), 404);
        } catch (DatabaseConnectionException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка подключения к БД при удалении: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Сервис временно недоступен",
                503
            );
        } catch (DataAccessException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Ошибка доступа к данным при удалении: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Не удалось удалить пользователя",
                500
            );
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            log.error(
                "Непредвиденная ошибка при удалении: {}",
                e.getMessage(), e
            );
            return DTO.error(
                "Внутренняя ошибка сервера",
                500
            );
        }
    }

    private void validate(UserRequest request, boolean isUpdate) {
        if (!isUpdate || request.getName() != null) {
            if (request.getName() == null || request.getName().isBlank()) {
                throw new ValidationException("Имя не может быть пустым");
            }
        }
        if (!isUpdate || request.getEmail() != null) {
            if (request.getEmail() == null
                    || !request.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$")) {
                throw new ValidationException(
                    "Некорректный email: " + request.getEmail()
                );
            }
        }
        if (request.getAge() != null) {
            if (request.getAge() < 0 || request.getAge() > 150) {
                throw new ValidationException(
                    "Возраст должен быть от 0 до 150 (получено: "
                    + request.getAge()
                    + ")"
                );
            }
        }
    }
}

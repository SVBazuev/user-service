package edu.example.infrastructure.repository;


import java.util.List;
import java.util.Optional;


import org.hibernate.SessionFactory;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.example.core.entity.User;
import edu.example.core.repository.UserRepository;
import edu.example.exception.DatabaseConnectionException;
import edu.example.exception.DataAccessException;
import edu.example.exception.UserNotFoundException;


public class HibernateUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(
        HibernateUserRepository.class
    );
    private final SessionFactory sessionFactory;

    public HibernateUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        try {
            sessionFactory.getCurrentSession()
                .persist(user);
            log.info("User saved: id={}", user.getId());
            return user;
        } catch (JDBCConnectionException e) {
            log.error("Database connection error while saving user", e);
            throw new DatabaseConnectionException(
                "Ошибка подключения к БД при сохранении",
                e
            );
        } catch (Exception e) {
            log.error("Error saving user", e);
            throw new DataAccessException(
                "Ошибка сохранения пользователя", e
            );
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User user = sessionFactory.getCurrentSession()
                .get(User.class, id);
            return Optional.ofNullable(user);
        } catch (JDBCConnectionException e) {
            throw new DatabaseConnectionException(
                "Ошибка подключения к БД при поиске пользователя",
                e
            );
        } catch (Exception e) {
            throw new DataAccessException(
                "Ошибка поиска пользователя по id " + id,
                e
            );
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return sessionFactory.getCurrentSession()
                .createQuery("from User", User.class)
                .list();
        } catch (JDBCConnectionException e) {
            throw new DatabaseConnectionException(
                "Ошибка подключения к БД при получении списка",
                e
            );
        } catch (Exception e) {
            throw new DataAccessException(
                "Ошибка получения списка пользователей",
                e
            );
        }
    }

    @Override
    public void update(User user) {
        try {
            sessionFactory.getCurrentSession()
                .merge(user);
            log.info("User updated: id={}", user.getId());
        } catch (JDBCConnectionException e) {
            throw new DatabaseConnectionException(
                "Ошибка подключения к БД при обновлении",
                e
            );
        } catch (Exception e) {
            throw new DataAccessException(
                "Ошибка обновления пользователя id=" + user.getId(),
                e
            );
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            User user = sessionFactory.getCurrentSession()
                .get(User.class, id);
            if (user == null) {
                throw new UserNotFoundException(id);
            }
            sessionFactory.getCurrentSession()
                .remove(user);
            log.info("User deleted: id={}", id);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (JDBCConnectionException e) {
            throw new DatabaseConnectionException(
                "Ошибка подключения к БД при удалении",
                e
            );
        } catch (Exception e) {
            throw new DataAccessException(
                "Ошибка удаления пользователя id=" + id,
                e
            );
        }
    }

    @Override
    public boolean existsById(Long id) {
        try {
            Long count = sessionFactory.getCurrentSession()
                .createQuery(
                    "select count(*) from User where id = :id", Long.class)
                .setParameter("id", id)
                .uniqueResult();
            return count != null && count > 0;
        } catch (JDBCConnectionException e) {
            throw new DatabaseConnectionException(
                "Ошибка подключения к БД при проверке существования",
                e
            );
        } catch (Exception e) {
            throw new DataAccessException(
                "Ошибка проверки существования пользователя id=" + id,
                e
            );
        }
    }
}

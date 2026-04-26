package edu.example.infrastructure.repository;


import edu.example.core.entity.User;
import edu.example.core.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
// import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class HibernateUserRepository implements UserRepository {
    private final SessionFactory sessionFactory;

    public HibernateUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from User", User.class).list();
    }

    @Override
    public void update(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(user);
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        if (user != null) session.remove(user);
    }

    @Override
    public boolean existsById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Long count = session.createQuery(
                "select count(*) from User where id = :id", Long.class)
                .setParameter("id", id)
                .uniqueResult();
        return count != null && count > 0;
    }
}

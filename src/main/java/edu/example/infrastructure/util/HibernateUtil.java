package edu.example.infrastructure.util;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.example.exception.DatabaseConnectionException;


public class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(
        HibernateUtil.class
    );
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            SessionFactory sf = configuration.buildSessionFactory();
            log.info("SessionFactory successfully created");
            return sf;
        } catch (Exception e) {
            log.error(
                "Initial SessionFactory creation failed: {}",
                e.getMessage(), e
            );
            throw new DatabaseConnectionException(
            "Не удалось создать SessionFactory. Проверьте подключение к БД.",
            e
            );
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            log.info("SessionFactory closed");
        }
    }

    private HibernateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

}

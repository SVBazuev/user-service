package edu.example.infrastructure.util;

import org.hibernate.SessionFactory;
import edu.example.infrastructure.configuration.HibernateConfiguration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = (
        HibernateConfiguration.sessionFactory()
    );

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) sessionFactory.close();
    }
}

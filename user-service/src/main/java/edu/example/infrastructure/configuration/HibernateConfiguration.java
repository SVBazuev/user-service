package edu.example.infrastructure.configuration;


import org.hibernate.SessionFactory;


import edu.example.core.entity.User;


// @Configuration
public class HibernateConfiguration {

    // @Bean
    public static SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = (
            new org.hibernate.cfg.Configuration()
        );

        configuration
            .addPackage("edu.example")
            .addAnnotatedClass(User.class)
            .setProperty(
                "hibernate.connection.driver_class",
                "org.postgresql.Driver"
            )
            .setProperty(
                "hibernate.connection.url",
                "jdbc:postgresql://localhost:5433/postgres?useUnicode=true&characterEncoding=UTF-8&charSet=utf8"
            )
            .setProperty(
                "hibernate.connection.username",
                "postgres"
            )
            .setProperty(
                "hibernate.connection.password",
                "password"
            )
            .setProperty(
                "hibernate.show_sql",
                "true"
            )
            .setProperty(
                "hibernate.hbm2ddl.auto",
                "update"
                // "create-drop"
            )
            .setProperty(
                "hibernate.current_session_context_class",
                "thread"
            );

        return configuration.buildSessionFactory();
    }
}

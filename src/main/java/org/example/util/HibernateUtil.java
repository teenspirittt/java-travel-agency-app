package org.example.util;
import org.example.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Employee.class);
            configuration.addAnnotatedClass(EmployeeTransfer.class);
            configuration.addAnnotatedClass(Hotel.class);
            configuration.addAnnotatedClass(Flight.class);
            configuration.addAnnotatedClass(Route.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Carrier.class);
            configuration.addAnnotatedClass(Aircraft.class);
            configuration.addAnnotatedClass(ClientRoute.class);

            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5433/tourism_db");
            configuration.setProperty("hibernate.connection.username", "tour_admin");
            configuration.setProperty("hibernate.connection.password", "tour");
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

            return configuration.buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Ошибка инициализации SessionFactory");
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}

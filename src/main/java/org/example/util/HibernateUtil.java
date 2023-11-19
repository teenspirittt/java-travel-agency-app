package org.example.util;
import org.example.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Создаем SessionFactory из hibernate.cfg.xml
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

            return configuration.buildSessionFactory();
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

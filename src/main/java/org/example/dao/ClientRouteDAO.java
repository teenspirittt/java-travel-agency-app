package org.example.dao;

import org.example.model.ClientRoute;
import org.example.model.ClientRouteId;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientRouteDAO {

    public void addClientRoute(ClientRoute clientRoute) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(clientRoute);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public ClientRoute getClientRouteById(ClientRouteId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ClientRoute.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateClientRoute(ClientRoute clientRoute) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(clientRoute);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteClientRoute(ClientRouteId id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ClientRoute clientRoute = session.get(ClientRoute.class, id);
            if (clientRoute != null) {
                session.delete(clientRoute);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}

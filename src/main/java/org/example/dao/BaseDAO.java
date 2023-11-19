package org.example.dao;

import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public abstract class BaseDAO<T> {

    protected abstract Class<T> getEntityClass();

    private void performTransaction(Operation<T> operation, T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            operation.perform(session, entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addEntity(T entity) {
        performTransaction(Session::save, entity);
    }

    protected T getEntityById(Long id) {
        return performQuery(session -> session.get(getEntityClass(), id));
    }

    protected void updateEntity(T entity) {
        performTransaction(Session::update, entity);
    }

    protected void deleteEntity(Long id) {
        performTransaction((session, e) -> {
            T entity = session.get(getEntityClass(), id);
            if (entity != null) {
                session.delete(entity);
            }
        }, null);
    }

    protected List<T> getAllEntities() {
        return performQuery(session -> {
            Query<T> query = session.createQuery("FROM " + getEntityClass().getSimpleName(), getEntityClass());
            return query.list();
        });
    }

    private interface Operation<T> {
        void perform(Session session, T entity);
    }


    private interface QueryOperation<R> {
        R perform(Session session);
    }

    private <R> R performQuery(QueryOperation<R> queryOperation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return queryOperation.perform(session);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

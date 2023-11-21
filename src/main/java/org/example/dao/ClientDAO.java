package org.example.dao;

import org.example.model.Client;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class ClientDAO extends BaseDAO<Client> {

    @Override
    protected Class<Client> getEntityClass() {
        return Client.class;
    }

    public List<Client> getClientByOrderDate(Date minDate, Date maxDate) {
        return performQuery(session -> {
            Query<Client> query = session.createQuery(
                    "FROM Client WHERE orderDate >= :minDate AND orderDate <= :maxDate",
                    Client.class
            );
            query.setParameter("minDate", minDate);
            query.setParameter("maxDate", maxDate);
            return query.list();
        });
    }
}

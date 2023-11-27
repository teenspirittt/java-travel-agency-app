package org.example.dao;

import org.example.model.ClientRoute;
import org.example.model.ClientRouteId;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientRouteDAO extends BaseDAO<ClientRoute>{

    @Override
    protected Class<ClientRoute> getEntityClass() {
        return ClientRoute.class;
    }
}

package org.example.dao;

import org.example.model.Aircraft;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class AircraftDAO extends BaseDAO<Aircraft> {

    @Override
    protected Class<Aircraft> getEntityClass() {
        return Aircraft.class;
    }

}

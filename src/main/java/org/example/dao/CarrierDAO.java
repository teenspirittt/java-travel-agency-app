package org.example.dao;

import org.example.model.Carrier;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CarrierDAO extends BaseDAO<Carrier>{

    @Override
    protected Class<Carrier> getEntityClass() {
        return Carrier.class;
    }

}

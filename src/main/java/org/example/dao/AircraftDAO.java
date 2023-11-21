package org.example.dao;

import org.example.model.Aircraft;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AircraftDAO extends BaseDAO<Aircraft> {

    @Override
    protected Class<Aircraft> getEntityClass() {
        return Aircraft.class;
    }

    public List<Aircraft> getAircraftByType(String aircraftType) {
        return performQuery(session -> {
            Query<Aircraft> query = session.createQuery("FROM Aircraft WHERE aircraftType = :type", Aircraft.class);
            query.setParameter("type", aircraftType);
            return query.list();
        });
    }

    public List<Aircraft> getAircraftByCarrier(Long carrierId) {
        return performQuery(session -> {
            Query<Aircraft> query = session.createQuery("FROM Aircraft WHERE carrier.id = :carrierId", Aircraft.class);
            query.setParameter("carrierId", carrierId);
            return query.list();
        });
    }

    public List<Aircraft> getAircraftByManufacturer(String manufacturer) {
        return performQuery(session -> {
            Query<Aircraft> query = session.createQuery("FROM Aircraft WHERE manufacturer = :manufacturer", Aircraft.class);
            query.setParameter("manufacturer", manufacturer);
            return query.list();
        });
    }

    public List<Aircraft> getAircraftByCapacity(int minCapacity, int maxCapacity) {
        return performQuery(session -> {
            Query<Aircraft> query = session.createQuery(
                    "FROM Aircraft WHERE capacity >= :minCapacity AND capacity <= :maxCapacity",
                    Aircraft.class
            );
            query.setParameter("minCapacity", minCapacity);
            query.setParameter("maxCapacity", maxCapacity);
            return query.list();
        });
    }
}

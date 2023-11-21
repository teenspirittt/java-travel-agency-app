package org.example.dao;

import org.example.model.Route;
import org.hibernate.query.Query;
import java.util.List;

public class RouteDAO extends BaseDAO<Route> {

    @Override
    protected Class<Route> getEntityClass() {
        return Route.class;
    }
    public List<Route> getRouteByCountry(String country) {
        return performQuery(session -> {
            Query<Route> query = session.createQuery(
                    "FROM Route WHERE country = :country",
                    Route.class
            );
            query.setParameter("country", country);
            return query.list();
        });
    }

    public List<Route> getRouteByCity(String city) {
        return performQuery(session -> {
            Query<Route> query = session.createQuery(
                    "FROM Route WHERE city = :city",
                    Route.class
            );
            query.setParameter("city", city);
            return query.list();
        });
    }
}

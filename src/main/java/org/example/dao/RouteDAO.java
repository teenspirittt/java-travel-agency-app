package org.example.dao;

import org.example.model.Route;

public class RouteDAO extends BaseDAO<Route> {

    @Override
    protected Class<Route> getEntityClass() {
        return Route.class;
    }

}

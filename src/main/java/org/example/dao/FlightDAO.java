package org.example.dao;

import org.example.model.Flight;

public class FlightDAO extends BaseDAO<Flight> {

    @Override
    protected Class<Flight> getEntityClass() {
        return Flight.class;
    }
}

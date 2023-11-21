package org.example.dao;

import org.example.model.Flight;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class FlightDAO extends BaseDAO<Flight> {

    @Override
    protected Class<Flight> getEntityClass() {
        return Flight.class;
    }

    public List<Flight> getFlightByAvailableSeats(int minSeats, int maxSeats) {
        return performQuery(session -> {
            Query<Flight> query = session.createQuery(
                    "FROM Flight WHERE availableSeats >= :minSeats AND availableSeats <= :maxSeats",
                    Flight.class
            );
            query.setParameter("minSeats", minSeats);
            query.setParameter("maxSeats", maxSeats);
            return query.list();
        });
    }

    public List<Flight> getFlightByDepartureDate(Date minDate, Date maxDate) {
        return performQuery(session -> {
            Query<Flight> query = session.createQuery(
                    "FROM Flight WHERE departureDate >= :minDate AND departureDate <= :maxDate",
                    Flight.class
            );
            query.setParameter("minDate", minDate);
            query.setParameter("maxDate", maxDate);
            return query.list();
        });
    }
}

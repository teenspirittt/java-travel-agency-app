package org.example.dao;

import org.hibernate.query.Query;

import java.util.List;
import org.example.model.Hotel;

public class HotelDAO extends BaseDAO<Hotel> {

    @Override
    protected Class<Hotel> getEntityClass() {
        return Hotel.class;
    }
    public List<Hotel> getHotelByClass(int hotelClass) {
        return performQuery(session -> {
            Query<Hotel> query = session.createQuery(
                    "FROM Hotel WHERE class = :hotelClass",
                    Hotel.class
            );
            query.setParameter("hotelClass", hotelClass);
            return query.list();
        });
    }
}

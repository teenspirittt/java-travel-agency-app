package org.example.dao;

import org.example.model.Hotel;

public class HotelDAO extends BaseDAO<Hotel> {

    @Override
    protected Class<Hotel> getEntityClass() {
        return Hotel.class;
    }

}

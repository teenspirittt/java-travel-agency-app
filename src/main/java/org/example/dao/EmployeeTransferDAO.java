package org.example.dao;

import org.example.model.EmployeeTransfer;

import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class EmployeeTransferDAO extends BaseDAO<EmployeeTransfer> {

    @Override
    protected Class<EmployeeTransfer> getEntityClass() {
        return EmployeeTransfer.class;
    }

    public List<EmployeeTransfer> getByOrderDate(Date minDate, Date maxDate) {
        return performQuery(session -> {
            Query<EmployeeTransfer> query = session.createQuery(
                    "FROM EmployeeTransfer WHERE orderDate >= :minDate AND orderDate <= :maxDate",
                    EmployeeTransfer.class
            );
            query.setParameter("minDate", minDate);
            query.setParameter("maxDate", maxDate);
            return query.list();
        });
    }
}

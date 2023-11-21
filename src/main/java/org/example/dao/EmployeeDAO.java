package org.example.dao;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import org.example.model.Employee;

public class EmployeeDAO extends BaseDAO<Employee> {
    @Override
    protected Class<Employee> getEntityClass() {
        return Employee.class;
    }

    public List<Employee> getByPosition(String position) {
        return performQuery(session -> {
            Query<Employee> query = session.createQuery(
                    "FROM Employee WHERE position = :position",
                    Employee.class
            );
            query.setParameter("position", position);
            return query.list();
        });
    }

    public List<Employee> getBySalary(double minSalary, double maxSalary) {
        return performQuery(session -> {
            Query<Employee> query = session.createQuery(
                    "FROM Employee WHERE salary >= :minSalary AND salary <= :maxSalary",
                    Employee.class
            );
            query.setParameter("minSalary", minSalary);
            query.setParameter("maxSalary", maxSalary);
            return query.list();
        });
    }
}

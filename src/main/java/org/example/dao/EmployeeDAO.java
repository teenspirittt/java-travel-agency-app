package org.example.dao;

import org.example.model.Employee;

public class EmployeeDAO extends BaseDAO<Employee> {
    @Override
    protected Class<Employee> getEntityClass() {
        return Employee.class;
    }
}

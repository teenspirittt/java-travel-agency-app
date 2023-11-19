package org.example.dao;

import org.example.model.EmployeeTransfer;

public class EmployeeTransferDAO extends BaseDAO<EmployeeTransfer> {

    @Override
    protected Class<EmployeeTransfer> getEntityClass() {
        return EmployeeTransfer.class;
    }
}

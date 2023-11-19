package org.example.dao;

import org.example.model.Client;

public class ClientDAO extends BaseDAO<Client> {

    @Override
    protected Class<Client> getEntityClass() {
        return Client.class;
    }
}

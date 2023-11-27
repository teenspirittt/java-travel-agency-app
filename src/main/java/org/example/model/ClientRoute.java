package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "client_route")
public class ClientRoute {

    @EmbeddedId
    private ClientRouteId id;

    @ManyToOne
    @MapsId("clientId")
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @MapsId("routeId")
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "seat")
    private String seat;

    public ClientRouteId getId() {
        return id;
    }

    public void setId(ClientRouteId id) {
        this.id = id;
    }
    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}

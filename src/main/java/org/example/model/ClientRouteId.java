package org.example.model;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClientRouteId implements Serializable {

    private Long clientId;
    private Long routeId;

    public ClientRouteId() {
    }

    public ClientRouteId(Long clientId, Long routeId) {
        this.clientId = clientId;
        this.routeId = routeId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientRouteId)) return false;
        ClientRouteId that = (ClientRouteId) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(routeId, that.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, routeId);
    }
}
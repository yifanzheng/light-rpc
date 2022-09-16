package top.yifan.rpc.domain;

import lombok.Data;

import java.util.Objects;

/**
 * Endpoint
 *
 * @author sz7v
 */
@Data
public class Endpoint {
    private final String host;
    private final int port;
    private Integer weight;

    public Endpoint(String host, int port) {
        this.host = host;
        this.port = port;
        this.weight = 1;
    }

    public String getAddress() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return port == endpoint.port && Objects.equals(host, endpoint.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

}

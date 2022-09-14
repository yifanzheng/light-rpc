package top.yifan.rpc.config;

import lombok.Data;
import lombok.ToString;

/**
 * @author Star Zheng
 */
@Data
@ToString
public class ServiceConfig {

    private String version = "";

    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}

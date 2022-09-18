package top.yifan.rpc.config;

import lombok.*;

/**
 * ServiceConfig
 *
 * @author Star Zheng
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class ServiceConfig {

    private String version = "";

    private String group = "";

    private int weight = 1;

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + group + version;
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}

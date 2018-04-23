package com.jahnelgroup.cartographer.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "cartographer")
@Data
public class CartographerProperties {

    private String protocol = "http";
    private String clusterNodes = "localhost";
    private String migrationLocation = "classpath*:/elasticsearch/mappings/*.json";

    public HttpHost[] buildHosts() {
        final Function<String, String[]> portFunction = str -> "https".equals(this.getProtocol()) ? new String[]{str, "-1"} : str.split(":");
        return Arrays.stream(this.getClusterNodes().split(","))
                .map(portFunction)
                .map(hostArgs -> new HttpHost(hostArgs[0], hostArgs.length == 2 ? Integer.parseInt(hostArgs[1]) : -1, this.getProtocol()))
                .collect(Collectors.toList())
                .toArray(new HttpHost[]{});
    }

}
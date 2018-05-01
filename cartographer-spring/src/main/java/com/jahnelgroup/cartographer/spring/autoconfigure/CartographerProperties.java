package com.jahnelgroup.cartographer.spring.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cartographer", ignoreUnknownFields = true)
public class CartographerProperties {

    private boolean clean = false;
    private boolean repair = false;
    private boolean takeSnapshot = false;

    private String protocol = "http";
    private String clusterNodes = "localhost:9200";
    private String migrationLocation = "elasticsearch/mappings";

    private String cartographerIndex = "cartographer";
    private String cartographerIndexMappingFile = "cartographer_V1_init.json";

    private String snapshotName = "cartographer";
}

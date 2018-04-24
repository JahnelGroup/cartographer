package com.jahnelgroup.cartographer.core.config;

import lombok.Data;
import org.apache.http.HttpHost;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class CartographerConfiguration {

    private String protocol = "http";
    private String clusterNodes = "localhost:9200";
    private String migrationLocation = "elasticsearch/mappings";
    private String migrationExtension = "json";
    private boolean takeSnapshot;

    private String schemaIndex = "cartographer_schema";
    private String schemaMappingFile = "schema_mapping.json";

    private String snapshotName = "cartographer_snapshot";
    private String elasticsearchListIndexesUri = "/_cat/indices";

    public HttpHost[] buildHosts() {
        final Function<String, String[]> portFunction = str -> "https".equals(this.getProtocol()) ? new String[]{str, "-1"} : str.split(":");
        return Arrays.stream(this.getClusterNodes().split(","))
                .map(portFunction)
                .map(hostArgs -> new HttpHost(hostArgs[0], hostArgs.length == 2 ? Integer.parseInt(hostArgs[1]) : -1, this.getProtocol()))
                .collect(Collectors.toList())
                .toArray(new HttpHost[]{});
    }

}
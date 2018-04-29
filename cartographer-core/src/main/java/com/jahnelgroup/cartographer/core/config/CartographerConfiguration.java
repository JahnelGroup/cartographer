package com.jahnelgroup.cartographer.core.config;

import lombok.Data;
import org.apache.http.HttpHost;

import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class CartographerConfiguration {

    private final static String PREFIX = "cartographer.";

    private boolean clean;
    private boolean takeSnapshot;

    private String protocol;
    private String clusterNodes;
    private String migrationLocation;


    private String cartographerIndex;
    private String cartographerIndexMappingFile;

    private String snapshotName;

    public CartographerConfiguration() throws InvalidPropertiesFormatException {
        clean = getBool("clean", false);

        takeSnapshot = getBool("snapshot", false);
        snapshotName = getString("snapshotName", "cartographer");

        protocol = getString("protocol", "http");
        clusterNodes = getString("host", "localhost:9200");
        migrationLocation = getString("migrations", "elasticsearch/mappings");

        cartographerIndex = getString("index", "cartographer");
        cartographerIndexMappingFile = getString("mappingFile", "cartographer_V1_init.json");
    }

    private String getString(String key, String defaultValue) throws InvalidPropertiesFormatException {
        try{
            return getProperty(key, defaultValue);
        }catch(Exception e){
            throw new InvalidPropertiesFormatException(e);
        }
    }

    private boolean getBool(String key, Boolean defaultValue) throws InvalidPropertiesFormatException {
        try{
            return Boolean.parseBoolean(getProperty(key, defaultValue.toString()));
        }catch(Exception e){
            throw new InvalidPropertiesFormatException(e);
        }
    }

    private String getProperty(String key, String defaultValue) {
        return System.getProperties().getProperty(PREFIX + key, defaultValue);
    }

    public HttpHost[] buildHosts() {
        final Function<String, String[]> portFunction = str -> "https".equals(this.getProtocol()) ? new String[]{str, "-1"} : str.split(":");
        return Arrays.stream(this.getClusterNodes().split(","))
                .map(portFunction)
                .map(hostArgs -> new HttpHost(hostArgs[0], hostArgs.length == 2 ? Integer.parseInt(hostArgs[1]) : -1, this.getProtocol()))
                .collect(Collectors.toList())
                .toArray(new HttpHost[]{});
    }

}
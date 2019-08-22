package com.jahnelgroup.cartographer.core.elasticsearch.index;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.settings.Settings;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class IndexDefinition {

    private String name;
    private List<String> aliases;
    private Map<String, Object> mappings;
    private Settings settings;

    public IndexDefinition(String name, GetIndexResponse response) {
        aliases = response.getAliases().get(name).stream().map(AliasMetaData::alias).collect(Collectors.toList());
        mappings = response.getMappings().get(name).sourceAsMap();
        settings = response.getSettings().get(name);
    }

}

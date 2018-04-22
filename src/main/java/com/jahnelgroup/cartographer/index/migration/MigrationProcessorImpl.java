package com.jahnelgroup.cartographer.index.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.config.CartographerProperties;
import com.jahnelgroup.cartographer.index.IndexMapping;
import com.jahnelgroup.cartographer.index.IndexService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class MigrationProcessorImpl implements MigrationProcessor, ApplicationListener<ApplicationReadyEvent> {

    private CartographerProperties cartographerProperties;
    private ResourceLoader resourceLoader;
    private IndexService indexService;
    private ObjectMapper objectMapper;

    class MetaInfo {
        String index, description; Integer version;
        MetaInfo(String index, Integer version, String description){
            this.index = index; this.version = version; this.description = description;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        migrate();
    }

    @Override
    public void migrate() {
        try{
            for(IndexMapping sm : fetchMapping()){
                if( indexService.mappingExists(sm.getIndex())){
                    log.info("Elasticsearch index already exists: index="+sm.getIndex());
                }else{
                    log.info("Applying elasticsearch mapping: index="+sm.getIndex()+", file=" + sm.getFile());
                    indexService.createIndex(sm.getIndex(), objectMapper.readTree(sm.getMappingJson()));
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private List<IndexMapping> fetchMapping() throws IOException {
        List<IndexMapping> mappings = new ArrayList<>();
        for(Resource res : fetchResources()){
            MetaInfo meta = parseMeta(res);
            mappings.add(new IndexMapping(res.getFilename(), meta.index, meta.description, meta.version,
                    StreamUtils.copyToString(res.getInputStream(), Charset.defaultCharset())));
        }
        return mappings;
    }

    private Resource[] fetchResources(){
        Resource[] resources = new Resource[0];

        try{
            resources = new PathMatchingResourcePatternResolver(
                    this.getClass().getClassLoader()).getResources(cartographerProperties.getMigrationLocation());
        }catch(Exception e){

        }

        if( resources == null || resources.length == 0 ){
            log.info("Did not find any elasticsearch mappings at {}", cartographerProperties.getMigrationLocation());
        }

        return resources;
    }

    private MetaInfo parseMeta(Resource res){
        String filename = res.getFilename();

        String[] splitName = filename.split("_");

        String index = splitName[0];
        Integer version = Integer.parseInt(splitName[1].substring(1));
        String description = splitName[2];

        return new MetaInfo(index, version, description);
    }
}

package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultMigrationFileLoader implements MigrationFileLoader {

    private CartographerConfiguration config;

    @Override
    public List<MigrationFile> fetchMigrations() throws IOException {
        List<MigrationFile> mappings = new ArrayList<>();
        for(Resource res : fetchResources()){
            String contents = StreamUtils.copyToString(res.getInputStream(), Charset.defaultCharset());
            mappings.add(new MigrationFile(res.getFile().getName(), contents));
        }
        return mappings;
    }

    private Resource[] fetchResources(){
        Resource[] resources = new Resource[0];
        try{
            resources = new PathMatchingResourcePatternResolver(
                    this.getClass().getClassLoader()).getResources(config.getMigrationLocation());
        }catch(Exception e){

        }

        if( resources == null || resources.length == 0 ){
            log.info("Did not find any elasticsearch mappings at {}", config.getMigrationLocation());
        }

        return resources;
    }

}

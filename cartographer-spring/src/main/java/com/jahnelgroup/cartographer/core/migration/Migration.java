package com.jahnelgroup.cartographer.core.migration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Migration {

    private MigrationFile migrationFile;
    private MigrationMetaInfo metaInfo;

}

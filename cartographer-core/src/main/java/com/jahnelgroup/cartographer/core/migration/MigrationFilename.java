package com.jahnelgroup.cartographer.core.migration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MigrationFilename {

    private String index;
    private Integer version;
    private String description;

}

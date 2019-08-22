package com.jahnelgroup.cartographer.core.migration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public
class MigrationContent {
    private String index;
    private String settings;
    private String mappings;
}

package com.jahnelgroup.cartographer.index;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexMapping {

    private String file;
    private String index;
    private String description;
    private Integer version;
    private String mappingJson;

}
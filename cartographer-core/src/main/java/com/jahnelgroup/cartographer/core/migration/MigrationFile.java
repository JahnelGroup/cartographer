package com.jahnelgroup.cartographer.core.migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString(exclude = "contents")
public class MigrationFile {

    private String filename;

    private String contents;

}

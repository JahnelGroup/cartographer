package com.jahnelgroup.cartographer.core.migration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class MigrationFile {

    private String filename;
    private String contents;

}
package com.jahnelgroup.cartographer.core.migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.File;

@Data
@AllArgsConstructor
@ToString(exclude = "contents")
public class MigrationFile {

    private String filename;

    private String contents;

}
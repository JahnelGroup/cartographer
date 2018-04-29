package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public interface MigrationAggregator {

    Map<String, SortedSet<Migration>> aggregate(List<Migration> migrations);

}

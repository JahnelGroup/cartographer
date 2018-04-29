package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.compare.DefaultMigrationComparator;

import java.util.*;

public class DefaultMigrationAggregator implements MigrationAggregator {

    @Override
    public Map<String, SortedSet<Migration>> aggregate(List<Migration> migrations) {
        Map<String, SortedSet<Migration>> aggs = new HashMap<>();

        for(Migration m : migrations){
            SortedSet<Migration> set = aggs.getOrDefault(m.getMetaInfo().getIndex(),
                    new TreeSet<>(new DefaultMigrationComparator()));

            set.add(m);

            aggs.put(m.getMetaInfo().getIndex(), set);
        }

        return aggs;
    }


}

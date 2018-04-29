package com.jahnelgroup.cartographer.core.migration.compare;

import com.jahnelgroup.cartographer.core.migration.Migration;

import java.util.Comparator;

public class DefaultMigrationComparator implements Comparator<Migration> {
    @Override
    public int compare(Migration m1, Migration m2) {
        return Integer.compare(m1.getMetaInfo().getVersion(),
                m2.getMetaInfo().getVersion());
    }
}
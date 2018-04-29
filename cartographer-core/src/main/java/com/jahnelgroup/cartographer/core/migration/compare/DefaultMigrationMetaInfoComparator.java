package com.jahnelgroup.cartographer.core.migration.compare;

import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

import java.util.Comparator;

public class DefaultMigrationMetaInfoComparator implements Comparator<MigrationMetaInfo> {
    @Override
    public int compare(MigrationMetaInfo m1, MigrationMetaInfo m2) {
        return Integer.compare(m1.getVersion(),
                m2.getVersion());
    }
}
package com.jahnelgroup.cartographer.core.migration.compare;

import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public interface MigrationEqualityProvider {

    boolean migrationsAreEqual(MigrationMetaInfo m1, MigrationMetaInfo m2);

}

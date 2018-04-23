package com.jahnelgroup.cartographer.core.migration.compare;

import com.jahnelgroup.cartographer.core.migration.Migration;

public interface MigrationEqualityProvider {

    boolean migrationsAreEqual(Migration m1, Migration m2);

}

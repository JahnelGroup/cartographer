package com.jahnelgroup.cartographer.migration.compare;

import com.jahnelgroup.cartographer.migration.Migration;

public interface MigrationEqualityProvider {

    boolean migrationsAreEqual(Migration m1, Migration m2);

}

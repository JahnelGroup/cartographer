package com.jahnelgroup.cartographer.core.migration.compare;

import com.jahnelgroup.cartographer.core.migration.Migration;

public class DefaultMigrationEqualityProvider implements MigrationEqualityProvider {

    @Override
    public boolean migrationsAreEqual(Migration m1, Migration m2) {
        if(nullChecksFail(m1, m2) ||
            nullChecksFail(m1.getMetaInfo(), m2.getMetaInfo()) ||
            nullChecksFail(m1.getMigrationFile(), m2.getMigrationFile()))
            return false;

        return
            m1.getMigrationFile().getFilename() .equals(m2.getMigrationFile().getFilename())    &&
            m1.getMetaInfo().getChecksum()      .equals(m2.getMetaInfo().getChecksum())         &&
            m1.getMetaInfo().getDescription()   .equals(m2.getMetaInfo().getDescription())      &&
            m1.getMetaInfo().getIndex()         .equals(m2.getMetaInfo().getIndex())            &&
            m1.getMetaInfo().getTimestamp()     .equals(m2.getMetaInfo().getTimestamp())        &&
            m1.getMetaInfo().getVersion()       .equals(m2.getMetaInfo().getVersion())
        ;

    }

    private boolean nullChecksFail(Object o1, Object o2){
        if( o1 == null && o2 == null ) return true;
        if( o1 == null && o2 != null ) return true;
        if( o1 != null && o2 == null ) return true;
        return false;
    }

}

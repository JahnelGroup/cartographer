package com.jahnelgroup.cartographer.core.migration.compare;

import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public class DefaultMigrationEqualityProvider implements MigrationEqualityProvider {

    @Override
    public boolean migrationsAreEqual(MigrationMetaInfo m1, MigrationMetaInfo m2) {
        if( nullChecksFail(m1, m2) ) return false;

        return
            m1.getDocumentId()    .equals(m2.getDocumentId())         &&
            m1.getChecksum()      .equals(m2.getChecksum())         &&
            m1.getDescription()   .equals(m2.getDescription())      &&
            m1.getIndex()         .equals(m2.getIndex())            &&
            m1.getTimestamp()     .equals(m2.getTimestamp())        &&
            m1.getVersion()       .equals(m2.getVersion())
        ;

    }

    private boolean nullChecksFail(Object o1, Object o2){
        if( o1 == null && o2 == null ) return true;
        if( o1 == null && o2 != null ) return true;
        if( o1 != null && o2 == null ) return true;
        return false;
    }

}

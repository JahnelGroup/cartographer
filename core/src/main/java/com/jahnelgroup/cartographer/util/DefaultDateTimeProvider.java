package com.jahnelgroup.cartographer.util;

import java.time.ZonedDateTime;

public class DefaultDateTimeProvider implements DateTimeProvider {
    @Override
    public ZonedDateTime now() {
        throw new UnsupportedOperationException();
    }
}

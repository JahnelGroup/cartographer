package com.jahnelgroup.cartographer.core.util;

import java.time.ZonedDateTime;

public class DefaultDateTimeProvider implements DateTimeProvider {
    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}

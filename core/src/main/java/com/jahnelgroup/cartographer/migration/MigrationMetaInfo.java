package com.jahnelgroup.cartographer.migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
public class MigrationMetaInfo {

    public static enum Status {
        PENDING,
        SUCCESS,
        FAILED;
    }

    @NonNull
    private String index;

    @NonNull
    private Integer version;

    @NonNull
    private String description;

    @NonNull
    private Integer checksum;

    @NonNull
    private ZonedDateTime timestamp;

    private Status status;

}

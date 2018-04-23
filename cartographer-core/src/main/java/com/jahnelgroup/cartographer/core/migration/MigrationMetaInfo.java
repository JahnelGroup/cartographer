package com.jahnelgroup.cartographer.core.migration;

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
        FAILED
    }

    private String documentId;

    @NonNull
    private String index;

    @NonNull
    private String filename;

    @NonNull
    private Integer version;

    @NonNull
    private String description;

    @NonNull
    private String checksum;

    @NonNull
    private ZonedDateTime timestamp;

    private Status status;

}

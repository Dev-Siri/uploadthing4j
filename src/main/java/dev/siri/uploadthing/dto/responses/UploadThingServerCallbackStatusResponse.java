package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

public class UploadThingServerCallbackStatusResponse {
    private final String status;

    public UploadThingServerCallbackStatusResponse(@NotNull String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

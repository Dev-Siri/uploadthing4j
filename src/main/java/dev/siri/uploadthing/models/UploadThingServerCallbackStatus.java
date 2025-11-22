package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;

public class UploadThingServerCallbackStatus {
    private String status;

    public UploadThingServerCallbackStatus() {}

    public UploadThingServerCallbackStatus(@NotNull String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

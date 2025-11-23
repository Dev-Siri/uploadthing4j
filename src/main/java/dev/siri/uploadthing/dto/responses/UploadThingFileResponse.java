package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

public class UploadThingFileResponse {
    private final String id;
    private final String key;
    private final String name;
    private final String customId;
    private final String status;

    public UploadThingFileResponse(@NotNull String id, @NotNull String key, @NotNull String name, @NotNull String customId, @NotNull String status) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.customId = customId;
        this.status = status;
    }

    @NotNull
    public String getCustomId() {
        return customId;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
}

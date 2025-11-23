package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;

public class UploadThingFile {
    private String id;
    private String key;
    private String name;
    private String customId;
    private String status;

    public UploadThingFile() {}

    public UploadThingFile(@NotNull String id, @NotNull String key, @NotNull String name, @NotNull String customId, @NotNull String status) {
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

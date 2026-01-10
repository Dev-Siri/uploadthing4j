package dev.siri.uploadthing.dto.requests;

import org.jetbrains.annotations.NotNull;

public class UploadThingUploadRequestFile {
    private final String name;
    private final Long size;
    private final String type;

    public UploadThingUploadRequestFile(@NotNull String name, @NotNull Long size, @NotNull String type) {
        this.name = name;
        this.size = size;
        this.type = type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Long getSize() {
        return size;
    }

    @NotNull
    public String getType() {
        return type;
    }
}

package dev.siri.uploadthing.dto.requests;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UploadThingDeleteFilesRequestBody {
    private final List<String> fileKeys;

    public UploadThingDeleteFilesRequestBody(@NotNull List<String> fileKeys) {
        this.fileKeys = fileKeys;
    }

    public List<String> getUpdates() {
        return fileKeys;
    }
}

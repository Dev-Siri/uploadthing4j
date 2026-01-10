package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UploadThingListFilesResponse {
    final private List<UploadThingFileResponse> files;

    public UploadThingListFilesResponse(@NotNull List<UploadThingFileResponse> files) {
        this.files = files;
    }

    @NotNull
    public List<UploadThingFileResponse> getFiles() {
        return files;
    }
}

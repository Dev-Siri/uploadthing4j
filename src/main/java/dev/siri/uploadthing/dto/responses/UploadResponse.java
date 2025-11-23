package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UploadResponse {
    private final List<PreparedUploadFileResponse> data;

    public UploadResponse(@NotNull List<PreparedUploadFileResponse> data) {
        this.data = data;
    }

    @NotNull
    public List<PreparedUploadFileResponse> getData() {
        return data;
    }
}

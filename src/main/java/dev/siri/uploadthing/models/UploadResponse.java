package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UploadResponse {
    private List<PreparedUploadFile> data;

    public UploadResponse() {}

    public UploadResponse(@NotNull List<PreparedUploadFile> data) {
        this.data = data;
    }

    @NotNull
    public List<PreparedUploadFile> getData() {
        return data;
    }
}

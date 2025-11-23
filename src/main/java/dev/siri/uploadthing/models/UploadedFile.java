package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;

public class UploadedFile {
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;

    public UploadedFile() {}

    public UploadedFile(@NotNull String fileName, @NotNull String fileUrl, @NotNull Long fileSize, @NotNull String fileType) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @NotNull
    public String getFileUrl() {
        return fileUrl;
    }

    @NotNull
    public Long getFileSize() {
        return fileSize;
    }

    @NotNull
    public String getFileType() {
        return fileType;
    }
}

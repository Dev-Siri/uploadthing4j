package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UploadThingFile {
    private String fileKey;
    private String fileName;
    private Integer fileSize;
    private String callbackUrl;
    private String callbackSlug;
    private String fileType;
    private String fileUrl;
    private String customId;

    public UploadThingFile() {}

    public UploadThingFile(@NotNull String fileKey, @NotNull String fileName, @NotNull Integer fileSize,  @NotNull String callbackUrl,
                           @NotNull String callbackSlug, @Nullable String fileType, @Nullable String fileUrl, @Nullable String customId) {
        this.fileKey = fileKey;
        this.fileName = fileName;
        this.fileSize =  fileSize;
        this.callbackUrl = callbackUrl;
        this.callbackSlug = callbackSlug;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.customId = customId;
    }

    @NotNull
    public String getFileKey() {
        return fileKey;
    }

    @Nullable
    public String getFileType() {
        return fileType;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @Nullable
    public String getCustomId() {
        return customId;
    }

    @NotNull
    public Integer getFileSize() {
        return fileSize;
    }

    @NotNull
    public String getCallbackSlug() {
        return callbackSlug;
    }

    @NotNull
    public String getCallbackUrl() {
        return callbackUrl;
    }

    @Nullable
    public String getFileUrl() {
        return fileUrl;
    }
}

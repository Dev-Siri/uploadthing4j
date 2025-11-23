package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

public class UsageInfoResponse {
    private final Integer totalBytes;
    private final Integer appTotalBytes;
    private final Integer filesUploaded;
    private final Integer limitBytes;

    public UsageInfoResponse(@NotNull Integer totalBytes, @NotNull Integer appTotalBytes, @NotNull Integer filesUploaded, @NotNull Integer limitBytes) {
        this.totalBytes = totalBytes;
        this.appTotalBytes = appTotalBytes;
        this.filesUploaded = filesUploaded;
        this.limitBytes = limitBytes;
    }

    @NotNull
    public Integer getAppTotalBytes() {
        return appTotalBytes;
    }

    @NotNull
    public Integer getFilesUploaded() {
        return filesUploaded;
    }

    @NotNull
    public Integer getLimitBytes() {
        return limitBytes;
    }

    @NotNull
    public Integer getTotalBytes() {
        return totalBytes;
    }
}

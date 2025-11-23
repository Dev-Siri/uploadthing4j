package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;

public class UsageInfo {
    private Integer totalBytes;
    private Integer appTotalBytes;
    private Integer filesUploaded;
    private Integer limitBytes;

    public UsageInfo() {}

    public UsageInfo(@NotNull Integer totalBytes, @NotNull Integer appTotalBytes, @NotNull Integer filesUploaded, @NotNull Integer limitBytes) {
        this.totalBytes = totalBytes;
        this.appTotalBytes = appTotalBytes;
        this.filesUploaded = filesUploaded;
        this.limitBytes = limitBytes;
    }


    public int getAppTotalBytes() {
        return appTotalBytes;
    }

    public int getFilesUploaded() {
        return filesUploaded;
    }

    public int getLimitBytes() {
        return limitBytes;
    }

    public int getTotalBytes() {
        return totalBytes;
    }
}

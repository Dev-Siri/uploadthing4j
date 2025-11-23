package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PreparedUploadFile {
    private String key;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private ContentDisposition contentDisposition;
    private String pollingJwt;
    private String pollingUrl;
    private String customId;
    private String url;
    private Map<String, String> fields;

    public PreparedUploadFile() {}

    public PreparedUploadFile(@NotNull String key, @NotNull String fileName, @Nullable String fileType, @NotNull String fileUrl, @NotNull String contentDisposition,
                              @NotNull String pollingJwt, @NotNull String pollingUrl, @Nullable String customId, @NotNull String url,
                              @NotNull Map<String, String> fields) {
        if (contentDisposition.equals("inline")) {
            this.contentDisposition = ContentDisposition.inline;
        } else if (contentDisposition.equals("attachment")) {
            this.contentDisposition = ContentDisposition.attachment;
        } else {
            throw new IllegalArgumentException("contentDisposition can only be 'inline' or 'attachment'");
        }

        this.key = key;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.pollingJwt = pollingJwt;
        this.pollingUrl = pollingUrl;
        this.customId = customId;
        this.url = url;
        this.fields = fields;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @Nullable
    public String getFileType() {
        return fileType;
    }

    @NotNull
    public String getFileUrl() {
        return fileUrl;
    }

    @Nullable
    public String getCustomId() {
        return customId;
    }

    @NotNull
    public ContentDisposition getContentDisposition() {
        return contentDisposition;
    }

    @NotNull
    public String getPollingJwt() {
        return pollingJwt;
    }

    @NotNull
    public String getPollingUrl() {
        return pollingUrl;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public Map<String, String> getFields() {
        return fields;
    }
}

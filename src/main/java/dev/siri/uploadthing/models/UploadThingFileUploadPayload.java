package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UploadThingFileUploadPayload {
    private List<UploadThingUploadRequestFile> files;
    private String acl;
    private String contentDisposition;

    public UploadThingFileUploadPayload() {}

    public UploadThingFileUploadPayload(@NotNull List<UploadThingUploadRequestFile> files, @NotNull String acl, @NotNull String contentDisposition) {
        this.files = files;
        this.acl = acl;
        this.contentDisposition = contentDisposition;
    }

    @NotNull
    public List<UploadThingUploadRequestFile> getFiles() {
        return files;
    }

    @NotNull
    public String getAcl() {
        return acl;
    }

    @NotNull
    public String getContentDisposition() {
        return contentDisposition;
    }
}

package dev.siri.uploadthing.dto.requests;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UploadThingFileUploadPayload {
    private final List<UploadThingUploadRequestFile> files;
    private final String acl;
    private final String contentDisposition;

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

package dev.siri.uploadthing.models.errors;

public class UploadThingNoFilesUploadedError extends RuntimeException {
    public UploadThingNoFilesUploadedError() {
        super("UploadThing Upload Error: No uploaded files were returned by UploadThing.");
    }
}

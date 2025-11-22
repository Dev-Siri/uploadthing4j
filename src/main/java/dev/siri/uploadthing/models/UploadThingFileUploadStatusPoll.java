package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UploadThingFileUploadStatusPoll {
    private UploadThingFileUploadStatusPollStatus status;
    private UploadThingFile file;

    public UploadThingFileUploadStatusPoll() {}

    public UploadThingFileUploadStatusPoll(@NotNull String status, @Nullable UploadThingFile file) {
        UploadThingFileUploadStatusPollStatus pollStatus;

        if (status.equals("done")) {
            pollStatus = UploadThingFileUploadStatusPollStatus.done;
        } else if (status.equals("still working")) {
            pollStatus = UploadThingFileUploadStatusPollStatus.stillWorking;
        } else {
            throw new IllegalArgumentException("status must be either 'done' or 'still working'");
        }

        this.status = pollStatus;
        this.file = file;
    }

    @NotNull
    public UploadThingFileUploadStatusPollStatus getStatus() {
        return status;
    }

    @Nullable
    public UploadThingFile getFile() {
        return file;
    }
}

package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UploadThingFileUploadStatusPoll {
    public enum PollStatus {
        done,
        stillWorking,
    }

    public static class UploadThingStatusPollFile {
        private final String fileKey;
        private final String fileName;
        private final Integer fileSize;
        private final String callbackUrl;
        private final String callbackSlug;
        private final String fileType;
        private final String fileUrl;
        private final String customId;

        public UploadThingStatusPollFile(@NotNull String fileKey, @NotNull String fileName, @NotNull Integer fileSize,  @NotNull String callbackUrl,
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

    private final PollStatus status;
    private final UploadThingStatusPollFile file;

    public UploadThingFileUploadStatusPoll(@NotNull String status, @Nullable UploadThingStatusPollFile file) {
        PollStatus pollStatus;

        if (status.equals("done")) {
            pollStatus = PollStatus.done;
        } else if (status.equals("still working")) {
            pollStatus = PollStatus.stillWorking;
        } else {
            throw new IllegalArgumentException("status must be either 'done' or 'still working'");
        }

        this.status = pollStatus;
        this.file = file;
    }

    @NotNull
    public PollStatus getStatus() {
        return status;
    }

    @Nullable
    public UploadThingStatusPollFile getFile() {
        return file;
    }
}

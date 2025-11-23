package dev.siri.uploadthing.dto.requests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileAccessRequestBody {
    private final String fileKey;
    private final String customId;
    private final Integer expiresIn;

    public FileAccessRequestBody(@NotNull String fileKey, @Nullable String customId, @NotNull Integer expiresIn) {
        this.fileKey = fileKey;
        this.customId = customId;
        this.expiresIn = expiresIn;
    }

    public static class Builder {
        private String fileKey;
        private String customId;
        private Integer expiresIn;

        public Builder fileKey(@NotNull String fileKey) {
            this.fileKey = fileKey;
            return this;
        }

        public Builder customId(@Nullable String customId) {
            this.customId = customId;
            return this;
        }

        public Builder expiresIn(@NotNull Integer expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public FileAccessRequestBody build() {
            if (fileKey == null) {
                throw new IllegalStateException("fileKey must not be null");
            }
            if (expiresIn == null) {
                throw new IllegalStateException("expiresIn must not be null");
            }
            if (fileKey.length() > 300) {
                throw new IllegalStateException("fileKey must not be over 300 characters in length");
            }
            if (customId != null && customId.length() > 128) {
                throw new IllegalStateException("customId if present must not be over 128 characters in length");
            }
            if (expiresIn < 1 || expiresIn > 604800) {
                throw new IllegalStateException("expiresIn must be min 1 and max 604800");
            }

            return new FileAccessRequestBody(fileKey, customId, expiresIn);
        }
    }

    @NotNull
    public String getFileKey() {
        return fileKey;
    }

    @Nullable
    public String getCustomId() {
        return customId;
    }

    @NotNull
    public Integer getExpiresIn() {
        return expiresIn;
    }
}
package dev.siri.uploadthing.dto.requests;

import org.jetbrains.annotations.NotNull;

public class UploadThingListFilesParameters {
    private final Integer limit;
    private final Integer offset;

    public UploadThingListFilesParameters(@NotNull Integer limit, @NotNull Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }

    @NotNull
    public Integer getLimit() {
        return limit;
    }

    @NotNull
    public Integer getOffset() {
        return offset;
    }
}

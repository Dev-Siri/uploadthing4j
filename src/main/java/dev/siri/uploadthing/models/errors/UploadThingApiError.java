package dev.siri.uploadthing.models.errors;

import dev.siri.uploadthing.models.ErrorData;
import dev.siri.uploadthing.models.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UploadThingApiError extends RuntimeException {
    @Nullable
    public List<ErrorData> data;

    public UploadThingApiError(ErrorResponse errorResponse) {
        super("UploadThing API Error: " + errorResponse.getError());
        this.data = errorResponse.getData();
    }
}

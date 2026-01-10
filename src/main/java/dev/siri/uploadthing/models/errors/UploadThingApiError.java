package dev.siri.uploadthing.models.errors;

import dev.siri.uploadthing.dto.responses.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UploadThingApiError extends RuntimeException {
    @Nullable
    public List<ErrorResponse.ErrorData> data;

    public UploadThingApiError(ErrorResponse errorResponse) {
        super("UploadThing API Error: " + errorResponse.getError());
        this.data = errorResponse.getData();
    }
}

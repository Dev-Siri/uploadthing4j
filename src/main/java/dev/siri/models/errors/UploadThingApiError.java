package dev.siri.models.errors;

import dev.siri.models.ErrorData;
import dev.siri.models.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class UploadThingApiError extends RuntimeException {
    @Nullable
    public ErrorData data;

    public UploadThingApiError(ErrorResponse errorResponse) {
        super("UploadThing API Error: " + errorResponse.getError());
        this.data = errorResponse.getData();
    }
}

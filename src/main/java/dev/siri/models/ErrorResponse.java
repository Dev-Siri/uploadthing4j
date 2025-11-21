package dev.siri.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ErrorResponse {
    /**
     * A stringified version of the error.
     *
     * <p><b>Example</b></p>
     * <pre>Invalid request input</pre>
     */
    @NotNull
    private String error;
    /**
     * Additional data related to the error
     * <p><b>Example</b></p>
     * <pre>
     * {
     *     "code": "invalid_type",
     *     "expected": "string",
     *     "received": "undefined",
     *     "path": [
     *          "x-uploadthing-api-key"
     *     ,
     *     "message": "You must provide an API key in the 'x-uploadthing-api-key' header"
     * }
     * </pre>
     */
    @Nullable
    private ErrorData data;

    public ErrorResponse(@NotNull String error, @Nullable ErrorData data) {
        this.error = error;
        this.data = data;
    }


    public @NotNull  String getError() {
        return error;
    }

    public @Nullable ErrorData getData() {
        return data;
    }
}

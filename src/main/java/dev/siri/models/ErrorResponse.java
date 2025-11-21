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
    private ErrorData data;

    public ErrorResponse() {}

    public ErrorResponse(@NotNull String error, @Nullable ErrorData data) {
        this.error = error;
        this.data = data;
    }


    @NotNull
    public String getError() {
        return error;
    }

    @Nullable
    public ErrorData getData() {
        return data;
    }
}

package dev.siri.uploadthing.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
     *     ],
     *     "message": "You must provide an API key in the 'x-uploadthing-api-key' header"
     * }
     * </pre>
     */
    private List<ErrorData> data;

    public ErrorResponse() {}

    public ErrorResponse(@NotNull String error, @Nullable List<ErrorData> data) {
        this.error = error;
        this.data = data;
    }

    @NotNull
    public String getError() {
        return error;
    }

    @Nullable
    public List<ErrorData> getData() {
        return data;
    }
}

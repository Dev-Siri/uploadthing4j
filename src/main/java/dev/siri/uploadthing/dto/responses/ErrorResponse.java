package dev.siri.uploadthing.dto.responses;

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
    private final String error;
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
    private final List<ErrorData> data;

    public static class ErrorData {
        private final String code;
        private final String expected;
        private final String received;
        private final List<String> path;
        private final String message;

        public ErrorData(String code, String expected, String received, List<String> path, String message) {
            this.code = code;
            this.expected = expected;
            this.received = received;
            this.path = path;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getExpected() {
            return expected;
        }

        public String getReceived() {
            return received;
        }

        public List<String> getPath() {
            return path;
        }

        public String getMessage() {
            return message;
        }
    }

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

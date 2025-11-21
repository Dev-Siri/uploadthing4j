package dev.siri.models;

public class ErrorData {
    private String code;
    private String expected;
    private String received;
    private String[] path;
    private String message;

    public ErrorData(String code, String expected, String received, String[] path, String message) {
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

    public String[] getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}

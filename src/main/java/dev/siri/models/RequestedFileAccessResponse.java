package dev.siri.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequestedFileAccessResponse {
    /**
     * The presigned GET URL. If the file is private, the URL will contain authentication parameters.
     *
     * <p><b>Example</b></p>
     * <pre>https://APP_ID.ufs.sh/f/FILE_KEY</pre>
     */
    private String ufsUrl;

    public RequestedFileAccessResponse() {}

    public RequestedFileAccessResponse(@NotNull String ufsUrl) {
        this.ufsUrl = ufsUrl;
    }

    @NotNull
    public String getUfsUrl() {
        return ufsUrl;
    }
}

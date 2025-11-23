package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

public class RequestedFileAccessResponse {
    /**
     * The presigned GET URL. If the file is private, the URL will contain authentication parameters.
     *
     * <p><b>Example</b></p>
     * <pre>https://APP_ID.ufs.sh/f/FILE_KEY</pre>
     */
    private final String ufsUrl;

    public RequestedFileAccessResponse(@NotNull String ufsUrl) {
        this.ufsUrl = ufsUrl;
    }

    @NotNull
    public String getUfsUrl() {
        return ufsUrl;
    }
}

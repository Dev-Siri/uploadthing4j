package dev.siri.uploadthing.dto.responses;

import org.jetbrains.annotations.NotNull;

public class AppInfoResponse {
    /**
     * The app ID.
     *
     * <p><b>Example</b></p>
     * <pre>MY_APP_123</pre>
     */
    private final String appId;
    /**
     * The app's default ACL.
     *
     * <p><b>Example</b></p>
     * <pre>public-read</pre>
     */
    private final String defaultACL;
    /**
     * Whether the app allows overriding the ACL on a per-request basis
     *
     * <p><b>Example</b></p>
     * <pre>false</pre>
     */
    private final Boolean allowACLOverride;

    public AppInfoResponse(@NotNull String appId, @NotNull String defaultACL, @NotNull Boolean allowACLOverride) {
        this.appId = appId;
        this.defaultACL = defaultACL;
        this.allowACLOverride = allowACLOverride;
    }

    @NotNull
    public String getAppId() {
        return appId;
    }

    @NotNull
    public String getDefaultACL() {
        return defaultACL;
    }

    @NotNull
    public Boolean getAllowACLOverride() {
        return allowACLOverride;
    }
}

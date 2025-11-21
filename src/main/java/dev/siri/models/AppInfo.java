package dev.siri.models;

import org.jetbrains.annotations.NotNull;

public class AppInfo {
    /**
     * The app ID.
     *
     * <p><b>Example</b></p>
     * <pre>MY_APP_123</pre>
     */
    private String appId;
    /**
     * The app's default ACL.
     *
     * <p><b>Example</b></p>
     * <pre>public-read</pre>
     */
    private String defaultACL;
    /**
     * Whether the app allows overriding the ACL on a per-request basis
     *
     * <p><b>Example</b></p>
     * <pre>false</pre>
     */
    private Boolean allowACLOverride;

    public AppInfo() {}

    public AppInfo(@NotNull String appId, @NotNull String defaultACL, @NotNull Boolean allowACLOverride) {
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

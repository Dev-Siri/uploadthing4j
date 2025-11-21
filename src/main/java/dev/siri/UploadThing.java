package dev.siri;

import com.google.gson.Gson;
import dev.siri.models.AppInfo;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UploadThing {
    private final static String UPLOADTHING_API_URL = "https://api.uploadthing.com";
    private final static String UPLOADTHING_API_HEADER_KEY = "X-Uploadthing-Api-Key";

    private final static AsyncHttpClient httpClient = new DefaultAsyncHttpClient();
    private static final Gson gson = new Gson();

    @NotNull
    private String apiKey;

    public UploadThing(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    public CompletableFuture<AppInfo> getAppInfo() {
        final String requestUrl = String.format("%s/v7/getAppInfo", UPLOADTHING_API_URL);

        return httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    String body = response.getResponseBody();
                    return gson.fromJson(body, AppInfo.class);
                });
    }
}

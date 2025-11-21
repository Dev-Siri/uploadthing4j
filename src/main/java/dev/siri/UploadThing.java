package dev.siri;

import com.google.gson.Gson;
import dev.siri.dto.FileAccessRequestBody;
import dev.siri.models.AppInfo;
import dev.siri.models.ErrorResponse;
import dev.siri.models.RequestedFileAccessResponse;
import dev.siri.models.errors.UploadThingApiError;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
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

    public CompletableFuture<RequestedFileAccessResponse> requestFileAccess(FileAccessRequestBody fileAccessBody) throws UploadThingApiError {
        final String requestUrl = String.format("%s/v6/requestFileAccess", UPLOADTHING_API_URL);

        return httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .setBody(gson.toJson(fileAccessBody))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return gson.fromJson(body, RequestedFileAccessResponse.class);
                });
    }

    public CompletableFuture<AppInfo> getAppInfo() throws UploadThingApiError {
        final String requestUrl = String.format("%s/v7/getAppInfo", UPLOADTHING_API_URL);

        return httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return gson.fromJson(body, AppInfo.class);
                });
    }
}

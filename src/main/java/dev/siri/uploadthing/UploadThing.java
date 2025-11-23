package dev.siri.uploadthing;

import com.google.gson.Gson;
import dev.siri.uploadthing.models.*;
import dev.siri.uploadthing.models.errors.UploadThingApiError;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.request.body.multipart.FilePart;
import org.asynchttpclient.request.body.multipart.StringPart;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UploadThing {
    private final static String UPLOADTHING_API_URL = "https://api.uploadthing.com";
    private final static String UPLOADTHING_API_HEADER_KEY = "X-Uploadthing-Api-Key";

    private final static AsyncHttpClient httpClient = new DefaultAsyncHttpClient();
    private static final Gson gson = new Gson();

    @NotNull
    final private String apiKey;

    /** The total number of files to be uploaded. */
    private Integer totalFiles;
    /** The number of files that have been uploaded. */
    private Integer uploadedFiles;

    private ArrayList<UploadedFile> uploadedFilesData = new ArrayList<>();

    public UploadThing(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getUploadedFiles() {
        return uploadedFiles;
    }

    public Integer getTotalFiles() {
        return totalFiles;
    }

    public ArrayList<UploadedFile> getUploadedFilesData() {
        return uploadedFilesData;
    }

    public CompletableFuture<Void> uploadFiles(List<File> files) throws UploadThingApiError, IOException {
        final List<UploadThingUploadRequestFile> filesData = new ArrayList<>();

        for (final File file : files) {
            final long fileSize = file.length();
            final String fileName = file.getName();
            final String mimeType = Files.probeContentType(file.toPath());

            final UploadThingUploadRequestFile dataFile = new UploadThingUploadRequestFile(fileName, fileSize, mimeType);
            filesData.add(dataFile);
        }

        final String requestUrl = String.format("%s/v6/uploadFiles", UPLOADTHING_API_URL);
        final UploadThingFileUploadPayload payload = new UploadThingFileUploadPayload(filesData, "public-read", "inline");

        final UploadResponse preparedResponse = httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .setHeader("Content-Type", "application/json")
                .setBody(gson.toJson(payload))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return gson.fromJson(body, UploadResponse.class);
                })
                .join();

        for (final PreparedUploadFile data : preparedResponse.getData()) {
            final String url = data.getUrl();
            final Map<String, String> fields = data.getFields(); // assuming PreparedUploadFile has getFields()

            final RequestBuilder builder = new RequestBuilder("POST");
            builder.setUrl(url);

            for (Map.Entry<String, String> entry : fields.entrySet()) {
                builder.addBodyPart(new StringPart(entry.getKey(), entry.getValue()));
            }

            File file = files
                    .stream()
                    .filter(f -> f.getName().equals(data.getFileName()))
                    .findFirst()
                    .orElseThrow();

            final String mimeType = Files.probeContentType(file.toPath());
            builder.addBodyPart(new FilePart("file", file, mimeType));

            httpClient.executeRequest(builder.build())
                    .toCompletableFuture()
                    .thenApply(response -> {
                        final int status = response.getStatusCode();
                        final String body = response.getResponseBody();

                        if (status != 200 && status != 204) {
                            throw new UploadThingApiError(
                                    gson.fromJson(body, ErrorResponse.class)
                            );
                        }

                        return null;
                    })
                    .join();

            uploadedFiles++;
            uploadedFilesData.add(new UploadedFile(data.getFileName(), data.getFileUrl(), file.length(), mimeType));
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<UploadThingServerCallbackStatus> getServerCallbackStatus(String authorization) throws UploadThingApiError {
        final String requestUrl = String.format("%s/v6/serverCallback", UPLOADTHING_API_URL);

        return httpClient.prepare("GET", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .setHeader("Authorization", authorization)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return gson.fromJson(body, UploadThingServerCallbackStatus.class);
                });
    }

    public CompletableFuture<UploadThingFileUploadStatusPoll> getUploadStatus(@NotNull String fileKey) throws UploadThingApiError {
        final String requestUrl = String.format("%s/v6/pollUpload/%s", UPLOADTHING_API_URL, fileKey);

        return httpClient.prepare("GET", requestUrl)
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

                    return gson.fromJson(body, UploadThingFileUploadStatusPoll.class);
                });
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

    public CompletableFuture<UsageInfo> getUsageInfo() throws UploadThingApiError {
        final String requestUrl = String.format("%s/v7/getUsageInfo", UPLOADTHING_API_URL);

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

                    return gson.fromJson(body, UsageInfo.class);
                });
    }
}

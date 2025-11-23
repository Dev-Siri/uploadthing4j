package dev.siri.uploadthing;

import com.google.gson.Gson;
import dev.siri.uploadthing.dto.requests.*;
import dev.siri.uploadthing.dto.responses.*;
import dev.siri.uploadthing.models.errors.UploadThingApiError;
import dev.siri.uploadthing.models.UploadedFile;
import dev.siri.uploadthing.models.errors.UploadThingNoFilesUploadedError;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.request.body.multipart.FilePart;
import org.asynchttpclient.request.body.multipart.StringPart;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

    public UploadThing(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    public static String getFileUrl(@NotNull String fileKey) {
        return "https://utfs.io/f/" + fileKey;
    }

    /** Upload a provided list of one or many files to UploadThing. */
    public List<UploadedFile> uploadFiles(@NotNull List<File> files) throws UploadThingApiError, UploadThingNoFilesUploadedError, IOException {
        final List<UploadThingUploadRequestFile> filesData = new ArrayList<>();
        final ArrayList<UploadedFile> uploadedFilesData = new ArrayList<>();

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

        for (final PreparedUploadFileResponse data : preparedResponse.getData()) {
            final String url = data.getUrl();
            final Map<String, String> fields = data.getFields();

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

            uploadedFilesData.add(new UploadedFile(data.getFileName(), data.getFileUrl(), file.length(), mimeType));
        }

        return uploadedFilesData;
    }

    /** Helper method to only upload one file to UploadThing. */
    public UploadedFile uploadFile(@NotNull File file) throws UploadThingApiError, IOException {
        final List<UploadedFile> uploadedFiles = uploadFiles(List.of(file));

        if (uploadedFiles.isEmpty()) {
            throw new UploadThingNoFilesUploadedError();
        }

        return uploadedFiles.getFirst();
    }

    /**
     * List all the files uploaded to UploadThing.
     * @param limit The number of files to list. Defaults to 500.
     * @param offset Number files to skip from the beginning. Defaults to 0.
     */
    public CompletableFuture<UploadThingListFilesResponse> listFiles(int limit, int offset) throws UploadThingApiError {
        final String requestUrl = String.format("%s/v6/listFiles", UPLOADTHING_API_URL);
        final UploadThingListFilesParameters listFilesParameters = new UploadThingListFilesParameters(limit, offset);

        return httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .setBody(gson.toJson(listFilesParameters))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return gson.fromJson(body, UploadThingListFilesResponse.class);
                });
    }

    public CompletableFuture<UploadThingListFilesResponse> listFiles(int limit) throws UploadThingApiError {
        return listFiles(limit, 0);
    }

    public CompletableFuture<UploadThingListFilesResponse> listFiles() throws UploadThingApiError {
        return listFiles(500, 0);
    }

    /**
     * Rename a Map of provided files tied to their fileKeys.
     *
     * <p><b>Example</b></p>
     * <pre>{@code
     *   final List<Map<String, String>> updates = List.of(
     *      Map.of(
     *           "fileKey", "FILE_KEY",
     *           "newName", "foo.png",
     *      ),
     *      Map.of(
     *          "fileKey": "FILE_KEY",
     *          "newName": "bar.jpg"
     *      )
     *   );
     *
     *   uploadThing.renameFiles(updates);
     * }</pre>
     */
    public CompletableFuture<Void> renameFiles(List<Map<String, String>> updates) throws UploadThingApiError {
        final String requestUrl = String.format("%s/v6/renameFiles", UPLOADTHING_API_URL);
        final UploadThingRenameFilesRequestBody requestBody = new UploadThingRenameFilesRequestBody(updates);

        return httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .setHeader("Content-Type", "application/json")
                .setBody(gson.toJson(requestBody))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return null;
                });
    }

    /**
     * Helper method to rename a single file.
     *
     * <p><b>Example</b></p>
     * <pre>{@code
     *   uploadThing.renameFile("FILE_KEY", "foo.png");
     * }</pre>
     */
    public CompletableFuture<Void> renameFile(@NotNull String fileKey, @NotNull String newName) throws UploadThingApiError {
        final List<Map<String, String>> renameParameters = List.of(Map.of(
                "fileKey", fileKey,
                "newName", newName
        ));

        return renameFiles(renameParameters);
    }

     /** Delete a list of files tied to their fileKeys from UploadThing. */
    public CompletableFuture<Void> deleteFiles(List<String> fileKeys) throws UploadThingApiError {
        final String requestUrl = String.format("%s/v6/deleteFiles", UPLOADTHING_API_URL);
        final UploadThingDeleteFilesRequestBody requestBody = new UploadThingDeleteFilesRequestBody(fileKeys);

        return httpClient.prepare("POST", requestUrl)
                .setHeader(UPLOADTHING_API_HEADER_KEY, apiKey)
                .setHeader("Content-Type", "application/json")
                .setBody(gson.toJson(requestBody))
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    final String body = response.getResponseBody();
                    final int statusCode = response.getStatusCode();

                    if (statusCode != 200) {
                        final ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
                        throw new UploadThingApiError(error);
                    }

                    return null;
                });
    }

    /** Helper method to delete a singular file tied to its fileKey from UploadThing. */
    public CompletableFuture<Void> deleteFile(String fileKey) throws UploadThingApiError {
        return deleteFiles(List.of(fileKey));
    }

    public CompletableFuture<UploadThingServerCallbackStatusResponse> getServerCallbackStatus(String authorization) throws UploadThingApiError {
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

                    return gson.fromJson(body, UploadThingServerCallbackStatusResponse.class);
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

    public CompletableFuture<AppInfoResponse> getAppInfo() throws UploadThingApiError {
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

                    return gson.fromJson(body, AppInfoResponse.class);
                });
    }

    public CompletableFuture<UsageInfoResponse> getUsageInfo() throws UploadThingApiError {
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

                    return gson.fromJson(body, UsageInfoResponse.class);
                });
    }
}

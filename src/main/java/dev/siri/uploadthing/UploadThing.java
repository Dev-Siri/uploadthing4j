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

public class UploadThing implements AutoCloseable {
    private final static String UPLOADTHING_API_URL = "https://api.uploadthing.com";
    private final static String UPLOADTHING_API_HEADER_KEY = "X-Uploadthing-Api-Key";

    private final AsyncHttpClient httpClient;
    private final Gson gson;

    @NotNull
    final private String apiKey;

    public UploadThing(@NotNull String apiKey) {
        this.gson = new Gson();
        this.httpClient = new DefaultAsyncHttpClient();
        this.apiKey = apiKey;
    }

    public UploadThing(@NotNull String apiKey, Gson gson) {
        this.gson = gson;
        this.httpClient = new DefaultAsyncHttpClient();
        this.apiKey = apiKey;
    }

    public UploadThing(@NotNull String apiKey, AsyncHttpClient httpClient) {
        this.gson = new Gson();
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    public UploadThing(@NotNull String apiKey, AsyncHttpClient httpClient, Gson gson) {
        this.gson = gson;
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    public static String getFileUrl(@NotNull String fileKey) {
        return "https://utfs.io/f/" + fileKey;
    }

    /**
     * Upload a provided list of one or many files to UploadThing.
     *
     * @throws UploadThingApiError             if the UploadThing API returns a non 200 response.
     * @throws UploadThingNoFilesUploadedError if none of the provided files could be uploaded.
     * @throws IOException                     if a file cannot be read or its MIME type cannot be determined.
     */
    public CompletableFuture<List<UploadedFile>> uploadFiles(@NotNull List<File> files) throws UploadThingApiError, UploadThingNoFilesUploadedError, IOException {
        final List<UploadThingUploadRequestFile> filesData = new ArrayList<>();
        final ArrayList<CompletableFuture<UploadedFile>> uploadedFilesFutures = new ArrayList<>();

        for (final File file : files) {
            final long fileSize = file.length();
            final String fileName = file.getName();
            final String mimeType = Files.probeContentType(file.toPath());

            final UploadThingUploadRequestFile dataFile = new UploadThingUploadRequestFile(fileName, fileSize, mimeType);
            filesData.add(dataFile);
        }

        final String requestUrl = String.format("%s/v6/uploadFiles", UPLOADTHING_API_URL);
        final UploadThingFileUploadPayload payload = new UploadThingFileUploadPayload(filesData, "public-read", "inline");

        final CompletableFuture<UploadResponse> preparedResponseFuture = httpClient.prepare("POST", requestUrl)
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
                });

        return preparedResponseFuture.thenCompose(preparedResponse -> {
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

                final String mimeType;
                try {
                    mimeType = Files.probeContentType(file.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                builder.addBodyPart(new FilePart("file", file, mimeType));

                final CompletableFuture<UploadedFile> future = httpClient.executeRequest(builder.build())
                        .toCompletableFuture()
                        .thenApply(response -> {
                            final int status = response.getStatusCode();
                            final String body = response.getResponseBody();

                            if (status != 200 && status != 204) {
                                throw new UploadThingApiError(
                                        gson.fromJson(body, ErrorResponse.class)
                                );
                            }

                            return new UploadedFile(
                                    data.getFileName(),
                                    data.getFileUrl(),
                                    file.length(),
                                    mimeType
                            );
                        });

                uploadedFilesFutures.add(future);
            }

            return CompletableFuture
                    .allOf(uploadedFilesFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v ->
                            uploadedFilesFutures.stream()
                                    .map(CompletableFuture::join)
                                    .toList()
                    );
        });
    }

    /**
     * Helper method to only upload one file to UploadThing.
     *
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     * @throws IOException         if a file cannot be read or its MIME type cannot be determined.
     */
    public CompletableFuture<UploadedFile> uploadFile(@NotNull File file) throws UploadThingApiError, IOException {
        final CompletableFuture<List<UploadedFile>> uploadedFilesFuture = uploadFiles(List.of(file));

        return uploadedFilesFuture.thenApply(uploadedFiles -> {
            if (uploadedFiles.isEmpty()) {
                throw new UploadThingNoFilesUploadedError();
            }
            return uploadedFiles.getFirst();
        });
    }

    /**
     * List all the files uploaded to UploadThing.
     *
     * @param limit  The number of files to list. Defaults to 500.
     * @param offset Number files to skip from the beginning. Defaults to 0.
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
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

    /**
     * List all the files uploaded to UploadThing.
     *
     * @param limit The number of files to list.
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
    public CompletableFuture<UploadThingListFilesResponse> listFiles(int limit) throws UploadThingApiError {
        return listFiles(limit, 0);
    }

    /**
     * List all the files uploaded to UploadThing.
     *
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
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
     *
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
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
     *
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
    public CompletableFuture<Void> renameFile(@NotNull String fileKey, @NotNull String newName) throws UploadThingApiError {
        final List<Map<String, String>> renameParameters = List.of(Map.of(
                "fileKey", fileKey,
                "newName", newName
        ));

        return renameFiles(renameParameters);
    }

    /**
     * Delete a list of files tied to their fileKeys from UploadThing.
     *
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
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

    /**
     * Helper method to delete a singular file tied to its fileKey from UploadThing.
     *
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
    public CompletableFuture<Void> deleteFile(String fileKey) throws UploadThingApiError {
        return deleteFiles(List.of(fileKey));
    }

    /**
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
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

    /**
     * @param fileKey The unique UploadThing key for the file.
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
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

    /**
     * @param fileAccessBody UploadThing request for retrieving a file's URL.
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
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

    /**
     * @throws UploadThingApiError if the UploadThing API returns a non 200 response.
     */
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

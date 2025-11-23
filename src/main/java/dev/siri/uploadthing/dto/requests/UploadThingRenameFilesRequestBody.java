package dev.siri.uploadthing.dto.requests;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class UploadThingRenameFilesRequestBody {
    private final List<Map<String, String>> updates;

    public UploadThingRenameFilesRequestBody(@NotNull List<Map<String, String>> updates) {
        this.updates = updates;
    }

    public List<Map<String, String>> getUpdates() {
        return updates;
    }
}

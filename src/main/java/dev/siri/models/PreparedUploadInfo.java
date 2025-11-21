package dev.siri.models;

public class PreparedUploadInfo {
    private String key;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private ContentDisposition contentDisposition;
    private String pollingJwt;
    private String pollingUrl;
    private String customId;
    private String url;
    //    private Fields;

    public String getKey() {
        return key;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }
}

package zc.ai.service.documents;

import java.time.LocalDateTime;

// FileMergeResponse.java
public class FileMergeResponse {
    private String mergedFileName;
    private long fileSize; // in bytes
    private LocalDateTime generatedTime;
    private String message;

    // getters and setters
    public String getMergedFileName() {
        return mergedFileName;
    }

    public void setMergedFileName(String mergedFileName) {
        this.mergedFileName = mergedFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
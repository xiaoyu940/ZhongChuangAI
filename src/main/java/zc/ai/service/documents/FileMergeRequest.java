package zc.ai.service.documents;

// FileMergeRequest.java
public class FileMergeRequest {
    private String sourceFileName;    // 被附加文件
    private String appendFileName;   // 附加文件

    // getters and setters
    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getAppendFileName() {
        return appendFileName;
    }

    public void setAppendFileName(String appendFileName) {
        this.appendFileName = appendFileName;
    }
}
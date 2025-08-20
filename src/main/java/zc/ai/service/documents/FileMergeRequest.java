package zc.ai.service.documents;

import java.util.List;

// FileMergeRequest.java
public class FileMergeRequest {
    private List<String> sourceFileName;    // 被附加文件
    private String appendFileName;   // 附加文件

    // getters and setters
    public List<String> getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(List<String> sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getAppendFileName() {
        return appendFileName;
    }

    public void setAppendFileName(String appendFileName) {
        this.appendFileName = appendFileName;
    }
}
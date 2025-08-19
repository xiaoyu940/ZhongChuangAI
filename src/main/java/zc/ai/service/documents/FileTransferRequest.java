package zc.ai.service.documents;

public class FileTransferRequest {
    private String sourceFileName;
    private String targetDirectory;

    // 必须提供 getter/setter
    public String getSourceFileName() { return sourceFileName; }
    public void setSourceFileName(String sourceFileName) { this.sourceFileName = sourceFileName; }
    public String getTargetDirectory() { return targetDirectory; }
    public void setTargetDirectory(String targetDirectory) { this.targetDirectory = targetDirectory; }
}
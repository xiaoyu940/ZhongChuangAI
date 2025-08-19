package zc.ai.service.documents;

// FileConversionRequest.java
public class FileConversionRequest {
    private String inputFileName;  // 输入的Markdown文件路径
    private String outputFileName; // 输出的PDF文件路径(可选)

    // Getters and Setters
    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
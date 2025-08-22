package zc.ai.service.documents;

import java.util.List;

public class SelectionResponse {
    private List<Integer> selectedPages;
    private String outputFileName;
    private String status;
    private String message;

    // 构造函数、getters和setters
    public SelectionResponse() {}

    public SelectionResponse(List<Integer> selectedPages, String outputFileName, String status, String message) {
        this.selectedPages = selectedPages;
        this.outputFileName = outputFileName;
        this.status = status;
        this.message = message;
    }

    public List<Integer> getSelectedPages() { return selectedPages; }
    public void setSelectedPages(List<Integer> selectedPages) { this.selectedPages = selectedPages; }

    public String getOutputFileName() { return outputFileName; }
    public void setOutputFileName(String outputFileName) { this.outputFileName = outputFileName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
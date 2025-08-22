package zc.ai.service.documents;

public class SelectionRequest {
    private String topic;
    private String metaFileName;
    private String outputFileName;
    private String pdfName;

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }



    // 构造函数、getters和setters
    public SelectionRequest() {}

    public SelectionRequest(String topic, String metaFileName,String pdfName,String outputFileName) {
        this.topic = topic;
        this.metaFileName = metaFileName;
        this.outputFileName = outputFileName;
        this.pdfName=pdfName;
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getMetaFileName() { return metaFileName; }
    public void setMetaFileName(String metaFileName) { this.metaFileName = metaFileName; }

    public String getOutputFileName() { return outputFileName; }
    public void setOutputFileName(String outputFileName) { this.outputFileName = outputFileName; }
}
package zc.ai.service.documents;

import java.util.Map;


public class ImageExtractRequest {
    private String pdfFilePath;      // PDF文件路径
    private Integer targetPage;      // 指定页码（null表示全部页面）
    private String outputDirectory;  // 输出目录
    private Map<String, String> imageMetas; // 图片元数据（key: 图片名, value: 描述）

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public Integer getTargetPage() {
        return targetPage;
    }

    public void setTargetPage(Integer targetPage) {
        this.targetPage = targetPage;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Map<String, String> getImageMetas() {
        return imageMetas;
    }

    public void setImageMetas(Map<String, String> imageMetas) {
        this.imageMetas = imageMetas;
    }
}
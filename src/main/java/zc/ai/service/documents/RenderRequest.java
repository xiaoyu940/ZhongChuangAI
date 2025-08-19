package zc.ai.service.documents;

public class RenderRequest {

    private String ragFileName;
    private String renderFileName;
    private String templateFileName;


    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }



    public void setRagFileName(String ragFileName) {
        this.ragFileName = ragFileName;
    }

    public void setRenderFileName(String renderFileName) {
        this.renderFileName = renderFileName;
    }

    public String getRagFileName() {
        return ragFileName;
    }

    public String getRenderFileName() {
        return renderFileName;
    }
}

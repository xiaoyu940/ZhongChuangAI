package zc.ai.service.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RagRequest {
    @NotBlank
    private String question;

    @NotBlank
    private String docName; // 或改为 MultipartFile 用于文件上传

    // getters and setters

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String documentPath) {
        this.docName = documentPath;
    }
}

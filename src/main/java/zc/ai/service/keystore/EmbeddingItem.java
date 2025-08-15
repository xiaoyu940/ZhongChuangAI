package zc.ai.service.keystore;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class EmbeddingItem {
    @NotBlank
    private String text;

    private Map<String, String> metadata;

    public String getText() {
        return text;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }


    // getters and setters
}

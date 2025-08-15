package zc.ai.service.keystore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public class AddEmbeddingRequest {
    @NotBlank
    private String collectionName;

    @NotBlank
    private String text;

    private Map<String, String> metadata;

    public String getCollectionName() {
        return collectionName;
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    // getters and setters
}


package zc.ai.service.keystore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BatchEmbeddingRequest {
    @NotBlank
    private String collectionName;

    @NotEmpty
    private List<EmbeddingItem> items;

    public String getCollectionName() {
        return collectionName;
    }

    public List<EmbeddingItem> getItems() {
        return items;
    }

    // getters and setters
}

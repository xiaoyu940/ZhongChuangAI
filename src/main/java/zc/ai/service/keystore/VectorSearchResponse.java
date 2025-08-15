package zc.ai.service.keystore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
public class VectorSearchResponse {
    private final int resultCount;
    private final long elapsedTime;
    private String collectionName;
    private List<EmbeddingResult> results;
    private long totalHits;
    private long searchTimeMs;

    public VectorSearchResponse(String collectionName,
                                List<EmbeddingResult> results,
                                int resultCount,
                                long elapsedTime) {
        this.collectionName = collectionName;
        this.results = results;
        this.resultCount = resultCount;
        this.elapsedTime = elapsedTime;
    }

}


package zc.ai.service.keystore;
import java.util.List;

public class VectorSearchResponse {
    public int getResultCount() {
        return resultCount;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<EmbeddingResult> getResults() {
        return results;
    }

    public void setResults(List<EmbeddingResult> results) {
        this.results = results;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    public long getSearchTimeMs() {
        return searchTimeMs;
    }

    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }

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


package zc.ai.service.keystore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public class VectorSearchRequest {
      @NotBlank
    private String collectionName;

    @NotEmpty
    private List<String> keywords;

    @Min(1)
    @Max(100)
    private int topN = 10;

    // 可添加其他过滤条件
    private Map<String, String> filters;

    public String getCollectionName() {
        return collectionName;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getTopN() {
        return topN;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }
}
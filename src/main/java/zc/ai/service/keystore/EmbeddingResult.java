package zc.ai.service.keystore;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class EmbeddingResult {
    private String id;
    private String text;
    private double score;
    private Map<String, Object> metadata;

    // 添加构造方法
    public EmbeddingResult(String embeddingId, String text, Double score, Map<String, Object> metadata) {
        this.id = embeddingId;
        this.text = text;
        this.score = score;
        this.metadata = metadata;
    }

}

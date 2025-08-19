package zc.ai.service.keystore;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class EmbeddingResult {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

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

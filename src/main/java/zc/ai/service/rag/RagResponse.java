package zc.ai.service.rag;

import lombok.Data;

import java.util.List;

public class RagResponse {
    private String answer;
    private List<String> sources; // 可选的引用来源

    public RagResponse(String s) {
        this.answer=s;
    }

    // getters and setters

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}
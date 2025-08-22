package zc.ai.service.example;

import java.util.List;

public class PageSummary {
    private int pageNum;
    private String tag;
    private String summary1;
    private String summary2;
    private String summary3;
    private List<Double> vector;

    public PageSummary(int pageNum, String tag, String summary1, String summary2, String summary3, List<Double> vector) {
        this.pageNum = pageNum;
        this.tag = tag;
        this.summary1 = summary1;
        this.summary2 = summary2;
        this.summary3 = summary3;
        this.vector = vector;
    }

    // Getters
    public int getPageNum() { return pageNum; }
    public String getTag() { return tag; }
    public String getSummary1() { return summary1; }
    public String getSummary2() { return summary2; }
    public String getSummary3() { return summary3; }
    public List<Double> getVector() { return vector; }

    @Override
    public String toString() {
        return String.format("页码: %d, 标签: %s, 摘要1: %s, 摘要2: %s, 摘要3: %s",
                pageNum, tag, summary1, summary2, summary3);
    }
}
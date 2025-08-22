package zc.ai.service.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用于存储每一页的标签信息的数据模型
 */
public class PdfPageTag {
    @JsonProperty("page_num")
    private int pageNum;

    @JsonProperty("tag")
    private String tag;

    // 默认构造函数（为JSON反序列化所需）
    public PdfPageTag() {}

    // 全参构造函数
    public PdfPageTag(int pageNum, String tag) {
        this.pageNum = pageNum;
        this.tag = tag;
    }

    // Getters and Setters (为JSON序列化所需)
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Page " + pageNum + ": " + tag;
    }
}
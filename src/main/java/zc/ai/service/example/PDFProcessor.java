package zc.ai.service.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PDFProcessor {

    // 读取目录JSON文件
    public static List<Map<String, Object>> readCatalog(String jsonPath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(jsonPath)));
        JSONArray jsonArray = JSON.parseArray(content);

        List<Map<String, Object>> catalog = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Map<String, Object> item = new HashMap<>();
            item.put("pageNum", obj.getInteger("pageNum"));
            item.put("tag", obj.getString("tag"));
            catalog.add(item);
        }

        return catalog;
    }

    // 提取PDF页面文本
    public static String extractPageText(String pdfPath, int pageNumber) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            if (pageNumber > document.getNumberOfPages() || pageNumber < 1) {
                throw new IOException("页码超出范围: " + pageNumber);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            return stripper.getText(document);
        }
    }

    // 处理单个页面，生成摘要和向量
    public static PageSummary processPage(DeepSeekClientExample client,int pageNum, String tag, String pdfPath) {
        try {
            // 提取PDF文本
            String pageText = extractPageText(pdfPath, pageNum);

            // 构建提示词
            String prompt = String.format("你是专业的文本标注员,回复的内容是纯文本的，只包含提示词要求的文本，不包含拟人的没有实习性的拟人回复，下面的页面包含一个原始标签，你可以参考该标签，对下面的文本内容总结三条中心含义，每条50到100个字，总结的中心思想用字符'|'进行分割，下面的页面原始的标签是 %s\n 页面内容如下：%s",
                    tag, pageText.substring(0, Math.min(800, pageText.length())));

            // 调用大模型
            String modelResponse = client.chat(prompt);
             System.out.println("回复内容----------------------》"+modelResponse);
            // 解析响应
            String[] summaries = parseModelResponse(modelResponse);

            // 生成组合文本的向量
            String combinedText = tag + " " + summaries[0] + " " + summaries[1] + " " + summaries[2];
            List<Double> vector = VectorUtils.textToVector(combinedText);

            return new PageSummary(pageNum, tag, summaries[0], summaries[1], summaries[2], vector);

        } catch (Exception e) {
            System.err.println("处理页码 " + pageNum + " 时出错: " + e.getMessage());
            // 生成降级摘要
            String[] fallbackSummaries = {
                    tag + " - 摘要1（处理异常）",
                    tag + " - 摘要2（处理异常）",
                    tag + " - 摘要3（处理异常）"
            };
            List<Double> fallbackVector = VectorUtils.textToVector(tag);
            return new PageSummary(pageNum, tag, fallbackSummaries[0], fallbackSummaries[1], fallbackSummaries[2], fallbackVector);
        }
    }
    public static String[] parseModelResponse(String response) {
        String[] parts = response.split("\\|");
        String[] summaries = new String[3];

        for (int i = 0; i < 3; i++) {
            if (i < parts.length) {

                summaries[i] = parts[i].trim();
                System.err.println("摘要" + i + " : " + summaries[i]);
            } else {
                System.err.println("无摘要");
                summaries[i] = "无摘要信息";
            }
        }

        return summaries;
    }
    // 保存向量数据到CSV文件
    public static void saveVectorsToCSV(List<PageSummary> summaries, String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("page_num,tag,summary1,summary2,summary3,vector");

            for (PageSummary summary : summaries) {
                String vectorStr = VectorUtils.vectorToString(summary.getVector());

                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"[%s]\"%n",
                        summary.getPageNum(),
                        summary.getTag().replace("\"", "\"\""),
                        summary.getSummary1().replace("\"", "\"\""),
                        summary.getSummary2().replace("\"", "\"\""),
                        summary.getSummary3().replace("\"", "\"\""),
                        vectorStr);
            }
        }
    }

    // 生成pgvector兼容的SQL文件
    public static void generatePgVectorSQL(List<PageSummary> summaries, String sqlPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(sqlPath))) {
            writer.println("-- 创建表");
            writer.println("CREATE TABLE pdf_vectors (");
            writer.println("    id SERIAL PRIMARY KEY,");
            writer.println("    page_num INTEGER NOT NULL,");
            writer.println("    tag TEXT,");
            writer.println("    summary1 TEXT,");
            writer.println("    summary2 TEXT,");
            writer.println("    summary3 TEXT,");
            writer.println("    vector vector(128)");
            writer.println(");");
            writer.println();

            writer.println("-- 插入数据");
            for (PageSummary summary : summaries) {
                String vectorStr = VectorUtils.vectorToString(summary.getVector());

                writer.printf("INSERT INTO pdf_vectors (page_num, tag, summary1, summary2, summary3, vector) " +
                                "VALUES (%d, '%s', '%s', '%s', '%s', '[%s]');%n",
                        summary.getPageNum(),
                        summary.getTag().replace("'", "''"),
                        summary.getSummary1().replace("'", "''"),
                        summary.getSummary2().replace("'", "''"),
                        summary.getSummary3().replace("'", "''"),
                        vectorStr);
            }
        }
    }

    // 搜索功能
    public static List<Integer> searchPages(List<PageSummary> summaries, String query, double threshold) {
        List<Double> queryVector = VectorUtils.textToVector(query);
        List<Integer> results = new ArrayList<>();

        for (PageSummary summary : summaries) {
            double similarity = VectorUtils.cosineSimilarity(queryVector, summary.getVector());
            if (similarity >= threshold) {
                results.add(summary.getPageNum());
            }
        }

        return results.stream().sorted().collect(Collectors.toList());
    }
}
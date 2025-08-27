package zc.ai.service.documents;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import zc.ai.service.example.DeepSeekClientExample;
import zc.ai.service.example.PDFProcessor;
import zc.ai.service.example.PageSummary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/pdf/")
public class PageDigestion{

    @PostMapping("/digest")
    public ResponseEntity<Map<String, Object>> processPdfVectorization(
            @RequestBody PdfProcessRequest request) {

        try {
            // 配置参数从请求体获取
            String catalogJsonPath = request.getCatalogJsonPath();
            String pdfPath = request.getPdfPath();
            String outputCsvPath = request.getOutputCsvPath();
            String outputSqlPath = request.getOutputSqlPath();

            System.out.println("开始处理PDF文档向量化...");

            // 1. 读取目录JSON
            List<Map<String, Object>> catalog = PDFProcessor.readCatalog(catalogJsonPath);
            System.out.println("成功读取目录，共 " + catalog.size() + " 个条目");

            // 2. 处理每个页面
            List<PageSummary> summaries = new ArrayList<>();

            DeepSeekClientExample client = new DeepSeekClientExample();

            AtomicInteger counter = new AtomicInteger(1);
            for (Map<String, Object> item : catalog) {
                int pageNum = (Integer) item.get("pageNum");
                String tag = (String) item.get("tag");

                System.out.println("正在处理页码: " + pageNum + ", 标签: " + tag);

                PageSummary summary = PDFProcessor.processPage(client, pageNum, tag, pdfPath);
                summaries.add(summary);
                counter.incrementAndGet();

                // 添加延迟，避免频繁调用
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return ResponseEntity.internalServerError()
                            .body(Map.of("error", "处理被中断"));
                }
            }

            // 3. 保存向量数据
            PDFProcessor.saveVectorsToCSV(summaries, outputCsvPath);
            System.out.println("向量数据已保存到: " + outputCsvPath);

            // 4. 生成pgvector SQL
            PDFProcessor.generatePgVectorSQL(summaries, outputSqlPath);
            System.out.println("pgvector SQL已生成到: " + outputSqlPath);

            // 返回处理结果
            Map<String, Object> response = Map.of(
                    "status", "success",
                    "totalPages", summaries.size(),
                    "csvPath", outputCsvPath,
                    "sqlPath", outputSqlPath,
                    "message", "PDF向量化处理完成"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("程序执行出错: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 请求参数类
    public static class PdfProcessRequest {
        private String catalogJsonPath;
        private String pdfPath;
        private String outputCsvPath;
        private String outputSqlPath;

        // getters and setters
        public String getCatalogJsonPath() { return catalogJsonPath; }
        public void setCatalogJsonPath(String catalogJsonPath) { this.catalogJsonPath = catalogJsonPath; }
        public String getPdfPath() { return pdfPath; }
        public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
        public String getOutputCsvPath() { return outputCsvPath; }
        public void setOutputCsvPath(String outputCsvPath) { this.outputCsvPath = outputCsvPath; }
        public String getOutputSqlPath() { return outputSqlPath; }
        public void setOutputSqlPath(String outputSqlPath) { this.outputSqlPath = outputSqlPath; }
    }

}
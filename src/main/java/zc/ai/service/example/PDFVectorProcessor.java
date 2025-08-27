package zc.ai.service.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PDFVectorProcessor {

    //按页逐页生成摘要
    public static void main(String[] args) {
        try {
            // 配置参数
            String catalogJsonPath = "C:\\ai-documents\\02.projects\\boc-report\\03.rag-files\\page_tags.json";
            String pdfPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\中国银行公司年报.pdf";
            String outputCsvPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\vectors.csv";
            String outputSqlPath = "C:\\ai-documents\\02.projects\\boc-report\\01.data-source\\vectors.sql";

            System.out.println("开始处理PDF文档向量化...");

            // 1. 读取目录JSON
            List<Map<String, Object>> catalog = PDFProcessor.readCatalog(catalogJsonPath);
            System.out.println("成功读取目录，共 " + catalog.size() + " 个条目");

            // 2. 处理每个页面
            java.util.List<PageSummary> summaries = new java.util.ArrayList<>();

            DeepSeekClientExample client = new DeepSeekClientExample();

            AtomicInteger i= new AtomicInteger(1);
            for (Map<String, Object> item : catalog) {
                int pageNum = (Integer) item.get("pageNum");
                String tag = (String) item.get("tag");

                System.out.println("正在处理页码: " + pageNum + ", 标签: " + tag);
               // new Thread(() -> {
                    PageSummary summary = PDFProcessor.processPage(client,pageNum, tag, pdfPath);
                    summaries.add(summary);
                    i.decrementAndGet();
               // }).start();

                i.addAndGet(1);

               // while(i.getAcquire()>=30){
                 //   Thread.sleep(10000);
               // }
                // 添加延迟，避免频繁调用
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            //while(summaries.size()<371){
             //   Thread.sleep(10000);
            //}
            // 3. 保存向量数据
            PDFProcessor.saveVectorsToCSV(summaries, outputCsvPath);
            System.out.println("向量数据已保存到: " + outputCsvPath);

            // 4. 生成pgvector SQL
            PDFProcessor.generatePgVectorSQL(summaries, outputSqlPath);
            System.out.println("pgvector SQL已生成到: " + outputSqlPath);

            // 5. 测试搜索功能
            System.out.println("\n=== 测试搜索功能 ===");
            String[] testQueries = {
                    "中国银行历史",
                    "风险管理",
                    "财务数据",
                    "荣誉奖项",
                    "公司治理"
            };

            for (String query : testQueries) {
                List<Integer> results = PDFProcessor.searchPages(summaries, query, 0.3);
                System.out.println("搜索: '" + query + "' -> 命中页码: " + results);
            }

            System.out.println("\n处理完成！");
            System.out.println("生成的CSV文件: " + outputCsvPath);
            System.out.println("生成的SQL文件: " + outputSqlPath);

        } catch (Exception e) {
            System.err.println("程序执行出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package zc.ai.service.documents;

// FileConversionService.java
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class FileConversionService {

    private static final String OUTPUT_DIR = "converted_files/";

    static {
        // 确保输出目录存在
        new File(OUTPUT_DIR).mkdirs();
    }

    public FileConversionResponse convertMarkdownToPdf(FileConversionRequest request) {
        LocalDateTime startTime = LocalDateTime.now();
        FileConversionResponse response = new FileConversionResponse();
        response.setInputFileName(request.getInputFileName());
        response.setGeneratedTime(startTime);

        try {
            // 验证输入文件
            if (!Files.exists(Paths.get(request.getInputFileName()))) {
                throw new FileNotFoundException("Input file not found: " + request.getInputFileName());
            }

            // 确定输出文件名
            String outputFileName = request.getOutputFileName();
            if (outputFileName == null || outputFileName.isEmpty()) {
                String inputName = new File(request.getInputFileName()).getName();
                outputFileName = OUTPUT_DIR + inputName.replace(".md", "") + "_" +
                        startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf";
            }

            // 1. 读取Markdown文件
            String markdownContent = FileUtils.readFileToString(new File(request.getInputFileName()), "UTF-8");

            // 2. 将Markdown转换为HTML
            MutableDataSet options = new MutableDataSet();
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            Node document = parser.parse(markdownContent);
            String htmlContent = renderer.render(document);

            // 添加基本HTML结构
            String fullHtml = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>" +
                    htmlContent + "</body></html>";

            // 3. 将HTML转换为PDF
            try (OutputStream os = new FileOutputStream(outputFileName)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withHtmlContent(fullHtml, null);
                builder.toStream(os);
                builder.run();
            }

            // 设置响应
            response.setOutputFileName(outputFileName);
            response.setFileSize(Files.size(Paths.get(outputFileName)));
            response.setSuccess(true);
            response.setMessage("File converted successfully in " +
                    java.time.Duration.between(startTime, LocalDateTime.now()).toMillis() + " ms");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Conversion failed: " + e.getMessage());
            response.setOutputFileName(null);
            response.setFileSize(0);
        }

        return response;
    }
}
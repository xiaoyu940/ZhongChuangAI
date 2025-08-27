package zc.ai.service.documents;

// FileConversionService.java
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class FileConversionService {
static Logger logger = LoggerFactory.getLogger(FileConversionService.class);
    @Value("${zc.ai.service.project-dir}")
 private String project_dir=null;
    public FileConversionResponse convertMarkdownToPdf(FileConversionRequest request) {

        request.setInputFileName(this.project_dir + request.getInputFileName());
        request.setOutputFileName(this.project_dir + request.getOutputFileName());

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
                outputFileName = inputName.replace(".md", "") + "_" +
                        startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf";
            }

            // 1. 读取Markdown文件
            String markdownContent = FileUtils.readFileToString(new File(request.getInputFileName()), "UTF-8");

            // 2. 将Markdown转换为HTML
            MutableDataSet options = new MutableDataSet();
            options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));

            // 设置表格渲染选项
            options.set(TablesExtension.CLASS_NAME, "markdown-table");
            options.set(TablesExtension.WITH_CAPTION, false);

            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            Node document = parser.parse(markdownContent);
            String htmlContent = renderer.render(document);
           // htmlContent = cleanHtmlTags(htmlContent);
            // 添加基本HTML结构
// 修改后的Markdown转HTML部分
            String fullHtml = Files.readString(
                    Paths.get(getClass().getResource("/templates/pdf-template.html").toURI()),
                    StandardCharsets.UTF_8
            );

            File msyh = Paths.get(getClass().getResource("/fonts/msyh.ttc").toURI()).toFile();
            File simsun = Paths.get(getClass().getResource("/fonts/simsun.ttc").toURI()).toFile();

            fullHtml=fullHtml.replace("<!-- CONTENT_PLACEHOLDER -->", htmlContent);
            // 3.


            //TODO bug here 字体无法正常设置
            try (OutputStream os = new FileOutputStream(outputFileName)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFont(simsun, "SimSun");
                builder.useFont(msyh, "Microsoft YaHei");

                // 添加字体别名
                builder.useFont(simsun, "宋体");
                builder.useFont(msyh, "微软雅黑");


                builder.withHtmlContent(fullHtml, null);
                builder.toStream(os);
                builder.run();

                logger.info("content is :"+builder.toString());
            }
            //TODO 先写一个html格式的
            Files.writeString(Path.of(request.getOutputFileName().replace("pdf","htm")),fullHtml);
            // 设置响应
            response.setOutputFileName(outputFileName);
            response.setFileSize(Files.size(Paths.get(outputFileName)));
            response.setSuccess(true);
            response.setMessage("File converted successfully in " +
                    java.time.Duration.between(startTime, LocalDateTime.now()).toMillis() + " ms");

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Conversion failed: " + e.getMessage());
            response.setOutputFileName(null);
            response.setFileSize(0);
        }

        return response;
    }

    private String cleanHtmlTags(String content) {
        // 使用 JSoup 清理 HTML，确保标签格式正确
        Safelist safelist = Safelist.relaxed()
                .addAttributes(":all", "style", "class", "id"); // 允许的属性

        // 清理并规范化 HTML
        String cleaned = Jsoup.clean(content, safelist);

        // 确保 br 标签正确闭合
        cleaned = cleaned.replaceAll("<br>", "<br/>")
                .replaceAll("<br\\s+/>", "<br/>");

        return cleaned;
    }
}
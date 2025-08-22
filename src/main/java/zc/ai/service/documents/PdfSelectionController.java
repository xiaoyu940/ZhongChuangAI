package zc.ai.service.documents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zc.ai.service.example.Assistant;
import zc.ai.service.example.DeepSeekClientExample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/doc/pdf")
public class PdfSelectionController {

    @Value("${zc.ai.service.project-dir}")
    private String projectDir;
    @Autowired
    private DeepSeekClientExample assistant;

    @Autowired
    private PdfPageProcessingService pdfProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/select-pages")
    public ResponseEntity<SelectionResponse> selectPages(@RequestBody SelectionRequest request) {
        try {
            // 1. 读取用户的topic
            String topic = request.getTopic();
            System.out.println("接收到的主题: " + topic);

            setAbsPath2File(request);//设置绝对路径
            // 2. 读取meta文件内容
            String metaContent = readMetaFile(request.getMetaFileName());
            System.out.println("读取meta文件成功，长度: " + metaContent.length());

            // 3. 构建提示词并调用大模型
            String prompt = buildPrompt(topic, metaContent);
            String modelResponse = assistant.chat(prompt);

            // 4. 解析大模型返回的页码列表
            List<Integer> selectedPages = parsePageNumbers(modelResponse);
            System.out.println("大模型选中的页码: " + selectedPages);

            // 5. 处理PDF文件并保存结果
            String pdfName = request.getPdfName();
            String outputFilePath = pdfProcessingService.extractAndSavePages(
                    selectedPages,1,pdfName, request.getOutputFileName());

            // 6. 返回响应
            SelectionResponse response = new SelectionResponse(
                    selectedPages,
                    request.getOutputFileName(),
                    "success",
                    "页面选择完成，结果已保存到: " + outputFilePath
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            SelectionResponse response = new SelectionResponse(
                    null,
                    null,
                    "error",
                    "处理失败: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private void setAbsPath2File(SelectionRequest request){
        request.setMetaFileName(this.projectDir+request.getMetaFileName());
        request.setOutputFileName(this.projectDir+request.getOutputFileName());
        request.setPdfName(this.projectDir+request.getPdfName());
    }

    public String readMetaFile(String metaFileName) throws IOException {
        // 假设meta文件存放在resources/meta目录下
        return new String(Files.readAllBytes(Paths.get(metaFileName)));
    }
    /**
     * 构建给大模型的提示词
     */
    private String buildPrompt(String topic, String metaContent) {
        return String.format(
                "你是一个专业的文档分析助手。请根据用户提供的主题，从以下文档页面元数据中选择最相关的页码。\n\n" +
                        "用户主题: %s\n\n" +
                        "文档页面元数据:\n%s\n\n" +
                        "请只返回一个JSON数组，包含选中的页码数字，例如: [1, 3, 5]。不要返回任何其他解释或文字。",
                topic, metaContent
        );
    }

    /**
     * 解析大模型返回的页码列表
     */
    private List<Integer> parsePageNumbers(String modelResponse) throws Exception {
        try {
            // 移除可能的markdown代码块标记
            String cleanResponse = modelResponse.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(cleanResponse, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            throw new Exception("解析大模型响应失败: " + modelResponse, e);
        }
    }
}
package zc.ai.service.documents;

import dev.langchain4j.service.spring.AiService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfig;
import zc.ai.service.example.Assistant;
import zc.ai.service.rag.RagResponse;
import zc.ai.service.rag.RagService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/docs")
public class RenderController {

    @Value("${zc.ai.service.project-dir}")
    private String projectDir;

    @Autowired
    private RagService ragService;

    @Autowired
    private Configuration freemarkerConfiguration; // 直接注入Configuration

    @PostMapping("/renderWithRagFile")
    public ResponseEntity<String> renderWithRagFile(@RequestBody RenderRequest request) throws IOException, TemplateException {

        String targetFileName = this.projectDir+request.getRenderFileName();

        String fileTmpFileName = request.getTemplateFileName();

        Path tmpFile=Path.of(this.projectDir+fileTmpFileName);
        if(!tmpFile.toFile().exists()){
            throw new TemplateException(null);
        }

        String fileTmpStr = Files.readString(tmpFile);
         // 2. 动态加载模板
        Template template = new Template("report",fileTmpStr,freemarkerConfiguration);

        String ragFile = request.getRagFileName();
        Assistant assistant = ragService.createAssistant(ragFile);
        // 3. 渲染模板
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("AI",assistant);

        String docTxt = FreeMarkerTemplateUtils.processTemplateIntoString(template, modelData);

        Files.writeString(Path.of(targetFileName),docTxt);

        return ResponseEntity.ok(targetFileName);
    }
}


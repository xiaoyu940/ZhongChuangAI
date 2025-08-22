package zc.ai.service.documents;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import zc.ai.service.chart.ChartService;
import zc.ai.service.example.Assistant;
import zc.ai.service.example.DeepSeekClientExample;
import zc.ai.service.rag.RagService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Autowired
    private DeepSeekClientExample dpAssistant ;

    @Autowired
    ConcurrentRenderer currRender;

    @Autowired
    ChartService chart =null;

    @PostMapping("/renderWithRagFile")
    public ResponseEntity<String> renderWithRagFile(@RequestBody RenderRequest request) throws Exception {

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
       // Assistant assistant = ragService.createAssistantWithFileContent(ragFile);
        Assistant assistant = ragService.createAssistant();
        // 3. 渲染模板
        Map<String, Object> modelData = new HashMap<>();
        //modelData.put("AI",assistant);
        modelData.put("AI",dpAssistant);
        modelData.put("CHART",chart);

//        String docTxt = FreeMarkerTemplateUtils.processTemplateIntoString(template, modelData);
        String docTxt = currRender.renderConcurrently(fileTmpStr,modelData);
        Files.writeString(Path.of(targetFileName),docTxt);

        return ResponseEntity.ok(targetFileName);
    }
}


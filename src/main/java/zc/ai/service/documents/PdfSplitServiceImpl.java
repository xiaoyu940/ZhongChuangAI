package zc.ai.service.documents;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PdfSplitServiceImpl implements DocumentSplitService {

    @Override
    public SplitResult splitDocument(SplitRequest request) {
        SplitResult result = new SplitResult();
        List<String> generatedFiles = new ArrayList<>();

        try {
            File inputFile = new File(request.getInputFilename());
            if (!inputFile.exists()) {
                throw new IOException("输入文件不存在: " + request.getInputFilename());
            }

            try (PDDocument document = PDDocument.load(inputFile)) {
                for (SplitRange range : request.getRanges()) {
                    // 处理PDF分割
                    String pdfOutputPath = processPdfSplit(document, range, request.getOutputDirectory());
                    generatedFiles.add(pdfOutputPath);

                    // 处理文本分割
                    String txtOutputPath = processTextSplit(document, range, request.getOutputDirectory());
                    generatedFiles.add(txtOutputPath);

                    // 处理元数据
                    String metaOutputPath = processMetaData(range, request.getOutputDirectory(),
                            getBaseFilename(txtOutputPath));
                    generatedFiles.add(metaOutputPath);
                }
            }

            result.setSuccess(true);
            result.setMessage("文档分割成功");
            result.setGeneratedFiles(generatedFiles);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("文档分割失败: " + e.getMessage());
        }

        return result;
    }

    private String processPdfSplit(PDDocument sourceDoc, SplitRange range, String outputDir) throws IOException {
        String outputPath = Paths.get(outputDir, range.getPdfFileName()).toString();

        try (PDDocument newDoc = new PDDocument()) {
            for (int i = range.getStartPage()-1; i < range.getEndPage(); i++) {
                newDoc.addPage(sourceDoc.getPage(i));
            }
            newDoc.save(outputPath);
        }

        return outputPath;
    }

    private String processTextSplit(PDDocument document, SplitRange range, String outputDir) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(range.getStartPage());
        stripper.setEndPage(range.getEndPage());

        String fullText = stripper.getText(document);
        String[] lines = fullText.split("\r?\n");

        // 应用行号控制
        int startLineIdx = (range.getStartLine() != null && range.getStartLine() > 0) ?
                range.getStartLine()-1 : 0;
        int endLineIdx = (range.getEndLine() != null && range.getEndLine() > 0 &&
                range.getEndLine() <= lines.length) ? range.getEndLine() : lines.length;

        String extractedText = String.join("\n", Arrays.copyOfRange(lines, startLineIdx, endLineIdx));

        // 使用LangChain4j进行智能分割
        DocumentSplitter splitter = DocumentSplitters.recursive(1000, 100);
        List<TextSegment> segments = splitter.split(Document.from(extractedText));

        String outputPath = Paths.get(outputDir, range.getTxtFileName()).toString();
        Files.writeString(Paths.get(outputPath),
                segments.stream().map(TextSegment::text).collect(Collectors.joining("\n\n")));

        return outputPath;
    }

    private String processMetaData(SplitRange range, String outputDir, String baseFilename) throws IOException {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("keywords", range.getKeywords());
        metaData.put("description", range.getDescription());

        // 修正路径处理
        String metaFilename = StringUtils.hasText(range.getMetaFileName()) ?
                range.getMetaFileName() :
                FilenameUtils.getBaseName(baseFilename) + ".meta";

        String outputPath = FilePathUtils.buildOutputPath(outputDir, metaFilename);

        // 确保父目录存在
        Path parentDir = Paths.get(outputPath).getParent();
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Files.writeString(
                Paths.get(outputPath),
                new ObjectMapper().writeValueAsString(metaData),
                StandardOpenOption.CREATE
        );

        return outputPath;
    }

    // 更新其他文件处理方法类似...

    private String getBaseFilename(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf('.'));
    }
}
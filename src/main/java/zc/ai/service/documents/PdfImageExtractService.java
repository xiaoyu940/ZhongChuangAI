package zc.ai.service.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PdfImageExtractService {

    //private static final Logger logger = (Logger) LoggerFactory.getLogger(PdfImageExtractService.class);

    public ImageExtractResult extractImages(ImageExtractRequest request) {
        ImageExtractResult result = new ImageExtractResult();
        List<ExtractedImageInfo> extractedImages = new ArrayList<>();

        try (PDDocument document = PDDocument.load(new File(request.getPdfFilePath()))) {
            PDFRenderer renderer = new PDFRenderer(document);
            int startPage = request.getTargetPage() != null ? request.getTargetPage() - 1 : 0;
            int endPage = request.getTargetPage() != null ? startPage + 1 : document.getNumberOfPages();

            for (int pageNum = startPage; pageNum < endPage; pageNum++) {
                PDPage page = document.getPage(pageNum);
                PDResources resources = page.getResources();

                int imageIndex = 0;
                for (COSName name : resources.getXObjectNames()) {
                    PDXObject xobject = resources.getXObject(name);
                    if (xobject instanceof PDImageXObject) {
                        imageIndex++;
                        ExtractedImageInfo imageInfo = processImage(
                                (PDImageXObject) xobject,
                                pageNum + 1,
                                imageIndex,
                                request
                        );
                        extractedImages.add(imageInfo);
                    }
                }
            }

            result.setSuccess(true);
            result.setMessage("成功提取 " + extractedImages.size() + " 张图片");
            result.setExtractedImages(extractedImages);

            // 生成结果报告文件
            generateResultReport(request, extractedImages);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("图片提取失败: " + e.getMessage());
            //logger.error("PDF图片提取异常", e);
        }

        return result;
    }

    private ExtractedImageInfo processImage(PDImageXObject image, int pageNum, int imageIndex,
                                            ImageExtractRequest request) throws IOException {
        // 1. 确定图片名称
        String imageName = determineImageName(image, pageNum, imageIndex);

        // 2. 构建输出路径
        Path outputPath = Paths.get(request.getOutputDirectory(), imageName + ".png");
        Files.createDirectories(outputPath.getParent());

        // 3. 保存图片
        ImageIO.write(image.getImage(), "png", outputPath.toFile());

        // 4. 保存元数据
        String metaContent = generateMetaContent(imageName, request);
        Path metaPath = Paths.get(request.getOutputDirectory(), imageName + ".meta");
        Files.writeString(metaPath, metaContent);

        return new ExtractedImageInfo(
                imageName,
                outputPath.toString(),
                metaPath.toString(),
                pageNum,
                imageIndex
        );
    }

    private String determineImageName(PDImageXObject image, int pageNum, int imageIndex) {
        // 尝试从图片注解获取名称
        String annotationName = extractImageAnnotationName(image);
        if (StringUtils.hasText(annotationName)) {
            return annotationName;
        }
        return String.format("page%d_img%d", pageNum, imageIndex);
    }

    private String extractImageAnnotationName(PDImageXObject image) {
        // 实现从PDImageXObject中提取注解的逻辑
        // 这里需要根据实际PDF结构实现
        return null;
    }

    private String generateMetaContent(String imageName, ImageExtractRequest request) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("keywords", request.getImageMetas().getOrDefault(imageName, ""));
        metaData.put("description", request.getImageMetas().getOrDefault(imageName, ""));

        try {
            return new ObjectMapper().writeValueAsString(metaData);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private void generateResultReport(ImageExtractRequest request,
                                      List<ExtractedImageInfo> images) throws IOException {
        Path reportPath = Paths.get(request.getOutputDirectory(), "extraction_report.json");
        Map<String, Object> report = new HashMap<>();
        report.put("pdfPath", request.getPdfFilePath());
        report.put("extractedImages", images.stream().map(img -> Map.of(
                "imageName", img.getImageName(),
                "imagePath", img.getImagePath(),
                "pageNumber", img.getPageNumber(),
                "imageIndex", img.getImageIndex()
        )).collect(Collectors.toList()));

        Files.writeString(reportPath, new ObjectMapper().writeValueAsString(report));
    }


    static class ExtractedImageInfo {
        private String imageName;
        private String imagePath;
        private String metaPath;
        private int pageNumber;
        private int imageIndex;

        public ExtractedImageInfo(String imageName, String imagePath, String metaPath, int pageNum, int imageIndex) {
            this.imageName=imageName;
            this.imagePath=imagePath;
            this.metaPath=metaPath;
            this.pageNumber=pageNum;
            this.imageIndex=imageIndex;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getMetaPath() {
            return metaPath;
        }

        public void setMetaPath(String metaPath) {
            this.metaPath = metaPath;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getImageIndex() {
            return imageIndex;
        }

        public void setImageIndex(int imageIndex) {
            this.imageIndex = imageIndex;
        }
    }
}
package zc.ai.service.documents;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PdfPageProcessingService {

    /**
     * 从PDF中提取指定页面的文本并保存为TXT文件
     * @param selectedPages 选中的页码列表
     * @param outputFileName 输出文件名
     * @return 保存的文件路径
     */
    public String extractAndSavePages(List<Integer> selectedPages,int pgOffset,String srcPdfName, String outputFileName) throws IOException {
        // 假设PDF文件存放在resources/pdfs目录下

        File pdfFile = Path.of(srcPdfName).toFile();
        File outputFile = Path.of(outputFileName).toFile();


        try (PDDocument document = PDDocument.load(pdfFile);
             FileWriter writer = new FileWriter(outputFile)) {

            PDFTextStripper textStripper = new PDFTextStripper();

            for (Integer pageNum : selectedPages) {
                if (pageNum > 0 && pageNum <= document.getNumberOfPages()) {
                    pageNum+=pgOffset;
                    textStripper.setStartPage(pageNum);
                    textStripper.setEndPage(pageNum);
                    String pageText = textStripper.getText(document);

                    writer.write("=== 第 " + pageNum + " 页 ===\n");
                    writer.write(pageText);
                    writer.write("\n\n");
                }
            }
        }

        return outputFile.getName();
    }
}
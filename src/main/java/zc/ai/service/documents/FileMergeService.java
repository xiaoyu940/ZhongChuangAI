package zc.ai.service.documents;

// FileMergeService.java
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class FileMergeService {

    private static final String OUTPUT_DIR = "merged_files/";

    static {
        // 确保输出目录存在
        new File(OUTPUT_DIR).mkdirs();
    }

    public FileMergeResponse mergeFiles(FileMergeRequest request) throws IOException {
        LocalDateTime startTime = LocalDateTime.now();
        String mergedFileName = generateMergedFileName(request.getSourceFileName());

        // 验证文件存在
        if (!Files.exists(Paths.get(request.getSourceFileName()))) {
            throw new FileNotFoundException("Source file not found: " + request.getSourceFileName());
        }

        if (!Files.exists(Paths.get(request.getAppendFileName()))) {
            throw new FileNotFoundException("Append file not found: " + request.getAppendFileName());
        }

        // 合并文件
        try (OutputStream out = new FileOutputStream(OUTPUT_DIR + mergedFileName);
             InputStream source = new FileInputStream(request.getSourceFileName());
             InputStream append = new FileInputStream(request.getAppendFileName())) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // 复制源文件
            while ((bytesRead = source.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            // 复制附加文件
            while ((bytesRead = append.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // 获取合并后文件信息
        Path mergedPath = Paths.get(OUTPUT_DIR + mergedFileName);
        long fileSize = Files.size(mergedPath);

        // 构建响应
        FileMergeResponse response = new FileMergeResponse();
        response.setMergedFileName(mergedFileName);
        response.setFileSize(fileSize);
        response.setGeneratedTime(LocalDateTime.now());
        response.setMessage("Files merged successfully in " +
                java.time.Duration.between(startTime, LocalDateTime.now()).toMillis() + " ms");

        return response;
    }

    private String generateMergedFileName(String sourceFileName) {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String extension = sourceFileName.substring(sourceFileName.lastIndexOf("."));
        return "merged_" + timestamp + extension;
    }
}
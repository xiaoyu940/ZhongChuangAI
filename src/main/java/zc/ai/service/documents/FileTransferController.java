package zc.ai.service.documents;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.*;

//移动文件
@RestController
public class FileTransferController {

    @Value("${zc.ai.service.project-dir}") // 默认基础目录
    private String baseDirectory;

    @PostMapping("/api/doc/transfer-file")
    public ResponseEntity<String> transferFile(@RequestBody FileTransferRequest request) {

        String sourceFileName = request.getSourceFileName();
        String targetDirectory = request.getTargetDirectory();
        try {
            // 构建完整路径
            Path sourcePath = Paths.get(baseDirectory, sourceFileName);
            Path targetPath = Paths.get(baseDirectory, targetDirectory);

            // 确保目标目录存在
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            // 构建目标文件路径
            Path destination = targetPath.resolve(sourcePath.getFileName());

            // 转移文件
            Files.move(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("文件转移成功: " + destination);

        } catch (NoSuchFileException e) {
            return ResponseEntity.badRequest().body("源文件不存在: " + e.getMessage());
        } catch (DirectoryNotEmptyException e) {
            return ResponseEntity.badRequest().body("目标目录不为空: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("文件转移失败: " + e.getMessage());
        }
    }
}
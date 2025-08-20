package zc.ai.service.documents;

// FileMergeService.java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileMergeService {

    @Value("${zc.ai.service.project-dir}")
    private   String project_dir=null;

    public FileMergeResponse mergeFiles(FileMergeRequest request) throws IOException {
//        request.setSourceFileName(this.project_dir + request.getSourceFileName());
        resetRequestFileName(request);
        request.setAppendFileName(this.project_dir + request.getAppendFileName());

        LocalDateTime startTime = LocalDateTime.now();


        if (!Files.exists(Paths.get(request.getAppendFileName()))) {
            Files.createFile(Path.of(request.getAppendFileName()));
        }

        // 获取append文件的原始大小（用于记录）
        long originalAppendFileSize = Files.size(Paths.get(request.getAppendFileName()));

        for(String srcFile:request.getSourceFileName()) {

            try (OutputStream out = new FileOutputStream(request.getAppendFileName(), true);
                 InputStream in = new FileInputStream(srcFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                System.out.println("文件追加完成！");

            } catch (IOException e) {
                System.err.println("发生错误: " + e.getMessage());
            }

        }

        // 获取追加后的文件信息
        long newFileSize = Files.size(Paths.get(request.getAppendFileName()));
        long appendedSize = newFileSize - originalAppendFileSize;

        // 构建响应
        FileMergeResponse response = new FileMergeResponse();
        response.setMergedFileName(request.getAppendFileName()); // 返回被追加的文件名
        response.setFileSize(newFileSize);
        response.setGeneratedTime(LocalDateTime.now());
        response.setMessage("Successfully appended " + appendedSize + " bytes from '" +
                request.getSourceFileName() + "' to '" + request.getAppendFileName() +
                "' in " + java.time.Duration.between(startTime, LocalDateTime.now()).toMillis() + " ms");

        return response;
    }

    private void resetRequestFileName(FileMergeRequest request){

        List<String> fileNames = request.getSourceFileName();
        List<String> newFileNames = new ArrayList<>();
        for(String fileName:fileNames){
            String newFileName = this.project_dir+fileName;
            newFileNames.add(newFileName);
        }
        request.setSourceFileName(newFileNames);
    }
}
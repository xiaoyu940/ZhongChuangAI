package zc.ai.service.documents;

import java.io.File;

public class FilePathUtils {
    public static String normalizePath(String path) {
        // 替换Windows路径分隔符为系统适配的分隔符
        return path.replace("\\", File.separator)
                .replace("/", File.separator)
                .replaceAll("[:*?\"<>|]", ""); // 移除非法字符
    }

    public static String buildOutputPath(String baseDir, String filename) {
        String normalizedDir = normalizePath(baseDir);
        String normalizedFile = normalizePath(filename);

        // 移除可能存在的重复分隔符
        if (normalizedDir.endsWith(File.separator)) {
            normalizedDir = normalizedDir.substring(0, normalizedDir.length()-1);
        }

        return normalizedDir + File.separator + normalizedFile;
    }

    public static void fileMove(String source,String target){
    }
}
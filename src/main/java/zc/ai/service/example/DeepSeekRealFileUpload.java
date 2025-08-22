package zc.ai.service.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class DeepSeekRealFileUpload {

    private static final String API_KEY = "sk-65e1d42bbf5b40a9b5d0f32332b17fac";
    private static final String CHAT_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String UPLOAD_URL = "https://api.deepseek.com/v1/files"; // DeepSeek文件上传端点

    /**
     * 真正的文件上传和分析流程
     */
    public static String analyzeWithRealFileUpload(File file, String instruction) throws Exception {
        // 1. 首先上传文件到DeepSeek
        String fileId = uploadFileToDeepSeek(file);

        // 2. 使用文件ID进行聊天分析
        return analyzeWithFileId(fileId, file.getName(), instruction);
    }

    /**
     * 上传文件到DeepSeek（真正的multipart/form-data上传）
     */
    private static String uploadFileToDeepSeek(File file) throws Exception {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        URL url = new URL(UPLOAD_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

            // 文件部分
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
            writer.append("Content-Type: " + Files.probeContentType(file.toPath())).append("\r\n");
            writer.append("\r\n").flush();

            // 写入文件内容
            Files.copy(file.toPath(), os);
            os.flush();

            writer.append("\r\n").flush();
            writer.append("--" + boundary + "--").append("\r\n").flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("id"); // 返回文件ID
            }
        } else {
            throw new RuntimeException("文件上传失败，HTTP代码: " + responseCode);
        }
    }

    /**
     * 使用文件ID进行分析
     */
    private static String analyzeWithFileId(String fileId, String fileName, String instruction) throws Exception {
        URL url = new URL(CHAT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setDoOutput(true);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");

        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");

        // DeepSeek文件附件格式
        JSONArray contentArray = new JSONArray();

        // 文本指令
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", instruction);
        contentArray.put(textContent);

        // 文件附件（真正的附件格式）
        JSONObject fileContent = new JSONObject();
        fileContent.put("type", "file");

        JSONObject fileData = new JSONObject();
        fileData.put("file_id", fileId);
        fileData.put("file_name", fileName);

        fileContent.put("file", fileData);
        contentArray.put(fileContent);

        userMessage.put("content", contentArray);
        messages.put(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 4000);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.toString().getBytes("utf-8"));
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } else {
            throw new RuntimeException("分析请求失败: " + connection.getResponseCode());
        }
    }

    /**
     * 批量文件上传和分析
     */
    public static String analyzeMultipleFiles(File[] files, String instruction) throws Exception {
        StringBuilder analysisResults = new StringBuilder();

        for (File file : files) {
            String fileId = uploadFileToDeepSeek(file);
            String analysis = analyzeWithFileId(fileId, file.getName(), instruction);

            analysisResults.append("=== 文件: ").append(file.getName()).append(" ===\n")
                    .append(analysis).append("\n\n");
        }

        return analysisResults.toString();
    }

    public static void main(String[] args) {
        try {
            // 创建测试文件
            File technicalDoc = new File("technical_document.pdf");
            // 这里应该是真实的PDF文件，为了演示我们创建一个文本文件
            try (PrintWriter writer = new PrintWriter(technicalDoc)) {
                writer.println("技术白皮书");
                writer.println("===========");
                writer.println("AI技术发展趋势分析");
                writer.println("深度神经网络架构优化");
                writer.println("大语言模型应用场景");
            }

            System.out.println("=== 真正的文件上传分析 ===");
            String instruction = "请分析这个技术文档的主要内容和技术要点";
            String analysis = analyzeWithRealFileUpload(technicalDoc, instruction);

            System.out.println("分析结果:\n" + analysis);

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
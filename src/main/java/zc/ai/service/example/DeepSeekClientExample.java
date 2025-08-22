package zc.ai.service.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.langchain4j.service.V;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import zc.ai.service.rag.RagService;

public class DeepSeekClientExample {
    private static final Logger logger =  LoggerFactory.getLogger(DeepSeekClientExample.class);
    @Value("${langchain4j.deepseek.api-key}")
    private  String API_KEY = "sk-65e1d42bbf5b40a9b5d0f32332b17fac";

    @Value("${langchain4j.deepseek.base-url}")
    private  String API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Value("${zc.ai.service.project-dir}")
    private String projectDir = null;

    public  String callDeepSeek(String prompt) {
        return callDeepSeek(prompt, "deepseek-chat", 0.7, 2000);
    }

    public  String callDeepSeek(String prompt, String model, double temperature, int maxTokens) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);

            requestBody.put("messages", messages);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // 解析响应
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject messageObj = firstChoice.getJSONObject("message");
                        return messageObj.getString("content");
                    }
                }
            } else {
                System.err.println("HTTP 错误代码: " + responseCode);
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.err.println("错误响应: " + errorResponse.toString());
                }
            }

        } catch (Exception e) {
            System.err.println("调用 DeepSeek API 时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public  String chat(String prompt) {
        String response = callDeepSeek(prompt);

        if (response != null) {
            System.out.println("DeepSeek 响应:");
            System.out.println(response);
        } else {
            System.out.println("调用失败");
        }
        return response;
    }


    public  String chatWithFile(String prompt,String fileName) {

        StringBuffer buf = new StringBuffer(prompt);
         try {
            String fileTxt = readFile(this.projectDir+fileName);
             buf.append(",请基于以下资料回答上述问题。资料如下:");
             buf.append(fileTxt);
             buf.append(fileTxt);
        }catch (Exception e){
             logger.error("can't find file in"+fileName);
        }

        String response = callDeepSeek(buf.toString());

        if (response != null) {
            System.out.println("DeepSeek 响应:");
            System.out.println(response);
        } else {
            System.out.println("调用失败");
        }
        return response;
    }

    private String readFile(String fileName) throws IOException {
        return Files.readString(Paths.get(fileName));
    }
}
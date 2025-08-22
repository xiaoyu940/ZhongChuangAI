package zc.ai.main.conf;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zc.ai.service.example.Assistant;
import zc.ai.service.example.DeepSeekClientExample;

import java.time.Duration;

@Configuration
public class DeepSeekModelConf {

    @Value("${langchain4j.deepseek.api-key}")
    private String apiKey;

    @Value("${langchain4j.deepseek.base-url}")  // 从配置读取
    private String baseUrl;

    @Value("${langchain4j.deepseek.model-name}")  // 从配置读取
    private String modelName;

    @Value("${langchain4j.deepseek.timeout}")
    private int timeout;
    @Bean
    public OpenAiChatModel openAiChatModel() {

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)  // 自定义地址
                .modelName(modelName)
                .timeout(Duration.ofMillis(timeout))
                .build();
    }

    @Bean
    public DeepSeekClientExample deepSeekAssitant() {

        DeepSeekClientExample example = new DeepSeekClientExample();
        return example;
    }


}
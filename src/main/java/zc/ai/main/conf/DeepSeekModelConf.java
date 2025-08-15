package zc.ai.main.conf;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepSeekModelConf {

    @Value("${langchain4j.deepseek.api-key}")
    private String apiKey;

    @Value("${langchain4j.deepseek.base-url}")  // 从配置读取
    private String baseUrl;

    @Value("${langchain4j.deepseek.model-name}")  // 从配置读取
    private String modelName;

    @Bean
    public OpenAiChatModel openAiChatModel() {

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)  // 自定义地址
                .modelName(modelName)
                .build();
    }
}
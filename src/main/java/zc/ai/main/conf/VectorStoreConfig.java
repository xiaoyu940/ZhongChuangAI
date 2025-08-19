package zc.ai.main.conf;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${vectorstore.dimension:384}")
    private int dimension;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {

        return PgVectorEmbeddingStore.builder()
                .host(extractHost(dbUrl))
                .port(extractPort(dbUrl))
                .user(username)
                .password(password)
                .database(extractDatabase(dbUrl))
                .table("zhongchuang_vector") // 基础表名，实际查询时会动态替换
                .dimension(dimension)
                .createTable(true)
                .build();
    }

    // 辅助方法从JDBC URL中提取连接信息
    private String extractHost(String jdbcUrl) {
        // 实现URL解析逻辑
        return "localhost";
    }

    private int extractPort(String jdbcUrl) {
        // 实现URL解析逻辑
        return 5432;
    }

    private String extractDatabase(String jdbcUrl) {
        // 实现URL解析逻辑
        return "vector_db";
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}
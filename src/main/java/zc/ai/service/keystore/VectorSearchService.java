package zc.ai.service.keystore;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorSearchService {

    @Autowired
    private  EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private   EmbeddingModel embeddingModel;

    @Value("${zc.ai.service.project-dir}")
    private String tmpStoreFileDir;

    public VectorSearchResponse search(VectorSearchRequest request) {
        long startTime = System.currentTimeMillis();

        // 1. 将关键词转为向量
        String queryText = String.join(" ", request.getKeywords());
        Embedding queryEmbedding = embeddingModel.embed(queryText).content();

        // 2. 构建搜索请求
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(request.getTopN())
                .minScore(0.3) // 可配置的相似度阈值
                .build();

        // 3. 执行搜索
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // 4. 转换结果
        List<EmbeddingResult> results = searchResult.matches().stream()
                .map(match -> new EmbeddingResult(
                        match.embeddingId(),
                        match.embedded() != null ? match.embedded().text() : null,
                        match.score(),
                        match.embedded() != null ? match.embedded().metadata().toMap() : null
                ))
                .collect(Collectors.toList());

        return new VectorSearchResponse(
                request.getCollectionName(),
                results,
                results.size(),
                System.currentTimeMillis() - startTime
        );
    }
    public String addEmbedding(String collectionName, String text, Map<String, String> metadata) {
        // 1. 将文本转换为向量
        Embedding embedding = embeddingModel.embed(text).content();

        // 2. 创建文本片段(包含元数据)
        TextSegment textSegment = TextSegment.from(text, new Metadata((metadata)));

        // 3. 存储到PGVector
        String embeddingId = embeddingStore.add(embedding, textSegment);

        return embeddingId;
    }


    public int batchAddEmbeddings(String collectionName, List<EmbeddingItem> items) {

        // 1. 批量生成向量
        List<Embedding> embeddings = items.stream()
                .map(item -> embeddingModel.embed(item.getText()).content())
                .collect(Collectors.toList());

        // 2. 准备文本片段
        List<TextSegment> textSegments = items.stream()
                .map(item -> TextSegment.from(item.getText(), new Metadata(item.getMetadata())))
                .collect(Collectors.toList());

        // 3. 批量存储
        List<String> ids = embeddingStore.addAll(embeddings, textSegments);

        return ids.size();
    }

    public List<Map<String, Object>> metasSearch(VectorSearchRequest request){

        VectorSearchResponse response = this.search(request);
        List<EmbeddingResult> results = response.getResults();
        List<Map<String,Object>> metas = new ArrayList<>();

        for(EmbeddingResult res:results){
            Map<String, Object> meta = res.getMetadata();
            metas.add(meta);
        }
        return  metas;
      }

    public String search2file(@Valid VectorSearchRequest request) {
        String fileName =request.getResultFileName();
        VectorSearchResponse result = this.search(request);
        String newFileName = write2file(fileName, result);
        return newFileName;
    }

    private String write2file(String fileName, VectorSearchResponse result){
        if (fileName == null|| StringUtils.isEmpty(fileName)) {
            fileName = "AI-"+System.currentTimeMillis() + ".txt";
        }
        File file = new File(this.tmpStoreFileDir + fileName);
        //fileName=file.getAbsolutePath();
        FileOutputStream out = null;
        try {
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            List<EmbeddingResult> retList = result.getResults();

            for (EmbeddingResult ret : retList) {
                out.write(ret.getText().getBytes());
                out.write("\n".getBytes());
            }

            return fileName;
        } catch (Exception e) {
             fileName=null;
        }finally{
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return fileName;
    }
}
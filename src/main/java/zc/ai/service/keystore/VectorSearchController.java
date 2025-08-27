package zc.ai.service.keystore;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vector")
public class VectorSearchController {

    @Autowired
    private final VectorSearchService searchService = null;

    @PostMapping("/search")
    public ResponseEntity<VectorSearchResponse> search(
            @RequestBody @Valid VectorSearchRequest request) {
        return ResponseEntity.ok(searchService.search(request));
    }

    //查询结果存入文件
    @PostMapping("/search2file")
    public ResponseEntity<String> seach2file(
            @RequestBody @Valid VectorSearchRequest request) {
        return ResponseEntity.ok(searchService.search2file(request));
    }



    @PostMapping("/add")
    public ResponseEntity<?> addEmbedding(@Valid @RequestBody AddEmbeddingRequest request) {
        String embeddingId = searchService.addEmbedding(
                request.getCollectionName(),
                request.getText(),
                request.getMetadata());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "embeddingId", embeddingId
        ));
    }

    @PostMapping("/batch-add")
    public ResponseEntity<?> batchAddEmbeddings(@Valid @RequestBody BatchEmbeddingRequest request) {
        int count = searchService.batchAddEmbeddings(
                request.getCollectionName(),
                request.getItems());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "count", count
        ));
    }
}
package zc.ai.service.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doc")
public class DocumentSplitController {

    @Autowired
    private DocumentSplitService documentSplitService;

    @PostMapping("/pdf-split")
    public ResponseEntity<SplitResult> splitDocument(@RequestBody SplitRequest request) {
        SplitResult result = documentSplitService.splitDocument(request);
        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }
}
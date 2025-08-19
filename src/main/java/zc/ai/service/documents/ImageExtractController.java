package zc.ai.service.documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf")
public class ImageExtractController {

    @Autowired
    private PdfImageExtractService imageExtractService;

    @PostMapping("/extract-images")
    public ResponseEntity<ImageExtractResult> extractImages(@RequestBody ImageExtractRequest request) {
        ImageExtractResult result = imageExtractService.extractImages(request);
        return result.isSuccess() ?
                ResponseEntity.ok(result) :
                ResponseEntity.badRequest().body(result);
    }
}
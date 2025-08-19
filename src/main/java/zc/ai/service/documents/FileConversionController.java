package zc.ai.service.documents;

// FileConversionController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doc/convert")
public class FileConversionController {

    @Autowired
    private FileConversionService conversionService;

    @PostMapping("/markdown-to-pdf")
    public ResponseEntity<FileConversionResponse> convertMarkdownToPdf(@RequestBody FileConversionRequest request) {
        FileConversionResponse response = conversionService.convertMarkdownToPdf(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
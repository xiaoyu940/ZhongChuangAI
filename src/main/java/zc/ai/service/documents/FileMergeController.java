package zc.ai.service.documents;

// FileMergeController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/doc")
public class FileMergeController {

    @Autowired
    private FileMergeService fileMergeService;

    @PostMapping("/merge")
    public ResponseEntity<FileMergeResponse> mergeFiles(@RequestBody FileMergeRequest request) {
        try {
            FileMergeResponse response = fileMergeService.mergeFiles(request);
            return ResponseEntity.ok(response);
        } catch (FileNotFoundException e) {
            FileMergeResponse errorResponse = new FileMergeResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (IOException e) {
            FileMergeResponse errorResponse = new FileMergeResponse();
            errorResponse.setMessage("Error merging files: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
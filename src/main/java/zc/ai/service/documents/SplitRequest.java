package zc.ai.service.documents;

import lombok.Data;
import java.util.List;

public class SplitRequest {
    private String inputFilename;
    private List<SplitRange> ranges;
    private String outputDirectory;

    public String getInputFilename() {
        return inputFilename;
    }

    public void setInputFilename(String inputFilename) {
        this.inputFilename = inputFilename;
    }

    public List<SplitRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<SplitRange> ranges) {
        this.ranges = ranges;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
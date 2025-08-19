package zc.ai.service.documents;

import java.util.List;

public class ImageExtractResult {

    private boolean success;
    private String message;
    private List<PdfImageExtractService.ExtractedImageInfo> extractedImages;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PdfImageExtractService.ExtractedImageInfo> getExtractedImages() {
        return extractedImages;
    }

    public void setExtractedImages(List<PdfImageExtractService.ExtractedImageInfo> extractedImages) {
        this.extractedImages = extractedImages;
    }


    public static class ExtractedImage {
        private String imageName;
        private String imagePath;
        private String metaPath;
        private int pageNumber;
        private int imageIndex;

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getMetaPath() {
            return metaPath;
        }

        public void setMetaPath(String metaPath) {
            this.metaPath = metaPath;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getImageIndex() {
            return imageIndex;
        }

        public void setImageIndex(int imageIndex) {
            this.imageIndex = imageIndex;
        }
    }
}
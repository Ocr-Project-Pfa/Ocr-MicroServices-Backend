package isme.pfaextract.Services;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FileUtils;
import java.awt.image.RescaleOp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrService.class);

    @Value("${ocr.tesseract.path}")
    private String tessPath;

    @Value("${ocr.tessdata.path}")
    private String tessDataPath;

    @Value("${ocr.language}")
    private String language;

    @Value("${ocr.oem}")
    private int oem;

    @Value("${ocr.psm}")
    private int psm;

    public String extractText(MultipartFile file) throws IOException, TesseractException {
        // Set the TESSDATA_PREFIX environment variable
        System.setProperty("TESSDATA_PREFIX", tessPath);

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);

        // Enable Arabic and French languages for OCR
        tesseract.setLanguage("ara+fra");

        // Set the OCR Engine Mode (OEM) and Page Segmentation Mode (PSM)
        tesseract.setOcrEngineMode(1); // LSTM-based engine for better accuracy

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.lastIndexOf('.') == -1) {
            log.error("File does not have a valid extension.");
            throw new IOException("Invalid file extension.");
        }

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        File tempFile = File.createTempFile("temp", fileExtension);
        file.transferTo(tempFile);

        log.info("Temporary file saved at: {}", tempFile.getAbsolutePath());
        log.info("File size: {} bytes", tempFile.length());

        // Read the image
        BufferedImage img = ImageIO.read(tempFile);
        if (img == null) {
            log.error("Failed to read the image file: {}", tempFile.getAbsolutePath());
            throw new IOException("Could not read the image.");
        }
        log.info("Successfully read the image: {}", tempFile.getAbsolutePath());

        // Image preprocessing
        BufferedImage preprocessedImg = preprocessImage(img);

        // Perform OCR
        String extractedText = tesseract.doOCR(preprocessedImg);
        log.info("Extracted text: {}", extractedText);

        // Clean up temporary file
        FileUtils.forceDelete(tempFile);

        return extractedText;
    }

    // Image preprocessing method: grayscale, threshold, etc.
    private BufferedImage preprocessImage(BufferedImage img) {
        // Convert to grayscale
        BufferedImage grayscaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscaleImg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        // Optional: Adjust contrast before thresholding
        grayscaleImg = adjustContrast(grayscaleImg);

        // Adaptive Thresholding: Adjust the threshold dynamically based on surrounding pixel intensity
        for (int x = 0; x < grayscaleImg.getWidth(); x++) {
            for (int y = 0; y < grayscaleImg.getHeight(); y++) {
                int pixel = grayscaleImg.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                int gray = (red + green + blue) / 3;

                // Adaptive thresholding based on local mean
                int localMean = getLocalMean(grayscaleImg, x, y, 5); // Adjust for local window size
                int threshold = (int) (localMean * 0.8);  // Adjust factor for better clarity
                if (gray > threshold) {
                    grayscaleImg.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    grayscaleImg.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return grayscaleImg;
    }

    // Helper method to compute the local mean intensity around a pixel
    private int getLocalMean(BufferedImage img, int x, int y, int windowSize) {
        int sum = 0;
        int count = 0;
        for (int i = -windowSize; i <= windowSize; i++) {
            for (int j = -windowSize; j <= windowSize; j++) {
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && ny >= 0 && nx < img.getWidth() && ny < img.getHeight()) {
                    int pixel = img.getRGB(nx, ny);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    int gray = (red + green + blue) / 3;
                    sum += gray;
                    count++;
                }
            }
        }
        return sum / count;
    }

    // Adjust contrast for better image clarity
    private BufferedImage adjustContrast(BufferedImage img) {
        RescaleOp rescaleOp = new RescaleOp(1.5f, 0, null); // Increase contrast
        rescaleOp.filter(img, img);
        return img;
    }
}

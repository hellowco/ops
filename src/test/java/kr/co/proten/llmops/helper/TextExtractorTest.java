package kr.co.proten.llmops.helper;

import kr.co.proten.llmops.core.helpers.TextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TextExtractorTest {

    private final TextExtractor textExtractor = new TextExtractor();
    private String savePath; // savePath 추가

    @BeforeEach
    void setUp() {
        savePath = System.getProperty("java.io.tmpdir"); // 운영 체제의 임시 디렉토리를 savePath로 설정
    }

    @AfterEach
    void cleanUpTempFiles() {
        File tempDir = new File(savePath);
        for (File file : tempDir.listFiles()) {
            if (file.getName().startsWith("test")) {
                file.delete();
            }
        }
    }

    @Test
    void testExtractTextFromPDF() throws Exception {
        File pdfFile = createValidPDF("test.pdf", "Dummy PDF Content");
        String extractedText = textExtractor.extractText(pdfFile, savePath);
        assertNotNull(extractedText);
        assertTrue(extractedText.contains("Dummy PDF Content"));
    }

    @Test
    void testExtractTextFromDOCX() throws Exception {
        File docxFile = createValidDOCX("test.docx", "Dummy DOCX Content");
        String extractedText = textExtractor.extractText(docxFile, savePath);
        assertNotNull(extractedText);
        assertTrue(extractedText.contains("Dummy DOCX Content"));
    }

    @Test
    void testExtractTextFromTXT() throws Exception {
        File txtFile = createTempFile("test.txt", "Dummy TXT Content");
        String extractedText = textExtractor.extractText(txtFile, savePath);
        assertEquals("Dummy TXT Content", extractedText);
    }

    private File createValidPDF(String filename, String content) throws IOException {
        File tempFile = new File(savePath, filename);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // 시스템 폰트 파일 경로 지정
            String fontPath = "C:/Windows/Fonts/arial.ttf"; // Windows의 경우
            // String fontPath = "/Library/Fonts/Arial.ttf"; // macOS의 경우
            // String fontPath = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"; // Linux의 경우

            PDType0Font font = PDType0Font.load(document, new File(fontPath));

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText(content);
            contentStream.endText();
            contentStream.close();

            document.save(tempFile);
        }

        return tempFile;
    }

    private File createValidDOCX(String filename, String content) throws IOException {
        File tempFile = new File(savePath, filename);

        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText(content);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                document.write(fos);
            }
        }

        return tempFile;
    }

    private File createTempFile(String filename, String content) throws IOException {
        File tempFile = new File(savePath, filename);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content.getBytes());
        }
        return tempFile;
    }
}

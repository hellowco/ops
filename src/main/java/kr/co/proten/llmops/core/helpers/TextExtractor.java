package kr.co.proten.llmops.core.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TextExtractor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String extractText(File file, String savePath) {
        String extension = getExtension(file.getName());
        try {
            return switch (extension) {
                case "pdf", "docx", "doc", "ppt", "pptx", "xls", "xlsx", "txt", "hwp", "hwpx" -> extractTextFrom(file, savePath);
                default -> throw new IllegalArgumentException("지원하지 않는 파일 형식: " + extension);
            };
        } catch (IOException e) {
            throw new RuntimeException("텍스트 추출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public String extractTextFrom(File file, String savePath) throws IOException {
        // OS에 따라 실행 파일 경로 설정
        String osName = System.getProperty("os.name").toLowerCase();
        String exeFileName;

        if (osName.contains("win")) {
            exeFileName = "snf/snf_exe.exe"; // Windows용 실행 파일
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            exeFileName = "snf/snf_exe"; // Linux/Mac용 실행 파일
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }

        // 리소스 경로에서 실행 파일을 복사해 임시 파일로 저장
        File tempExeFile = File.createTempFile("snf_exe", osName.contains("win") ? ".exe" : "");
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            // 리소스 파일을 임시 파일로 복사
            Files.copy(Objects.requireNonNull(classLoader.getResourceAsStream(exeFileName)), tempExeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempExeFile.setExecutable(true); // 실행 권한 설정
        } catch (NullPointerException e) {
            throw new IOException("Executable file not found in resources: " + exeFileName, e);
        }

        // 실행 인자 설정
        String arg1 = file.getAbsolutePath();
        String arg2 = "-NO_WITHPAGE";
        String arg3 = "-U8";

        Path saveDir = Paths.get(savePath);
        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir);
        }

        // 결과 저장할 파일 경로
        File resultFile = new File(saveDir+ "/" + removeFileExtension(file.getName()) + ".txt"); // 결과를 저장할 파일

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(List.of(tempExeFile.getAbsolutePath(), arg1, arg2, arg3));

            // 결과를 파일로 리다이렉트
            processBuilder.redirectOutput(resultFile);
            processBuilder.redirectErrorStream(true); // 표준 에러도 동일한 파일에 저장

            // 프로세스 실행
            Process process = processBuilder.start();

            // 프로세스가 완료될 때까지 대기
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Executable with arguments launched successfully. Output saved to: {}", resultFile.getAbsolutePath());
            } else {
                log.info("Executable failed with exit code: {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.info("Failed to launch executable: {}", e.getMessage());
        } finally {
            // 임시 파일 삭제
            tempExeFile.delete();
        }

        return resultFile.getAbsolutePath(); // 결과 파일 경로 반환
    }

    private String extractTextFromPDF(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractTextFromPDFWithPage(File file) throws IOException {
        Map<String, Object> fileMap = new HashMap<>();
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setParagraphStart("/pstart");
            pdfTextStripper.setParagraphEnd("/pend");
            pdfTextStripper.setLineSeparator("/lsep");

            int pdfTotalPages = document.getNumberOfPages();

            return new PDFTextStripper().getText(document);
        }
    }

    private String extractTextFromDOCX(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder sb = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                log.info("Extracted Text from docx: {}", paragraph.getText());
                sb.append(paragraph.getText()).append("\n");
            }
            return sb.toString();
        }
    }

    private String extractTextFromTXT(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    private String removeFileExtension(String fileName) {
        // 마지막 점(.) 위치 찾기
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName; // 확장자가 없으면 원본 이름 반환
    }
}

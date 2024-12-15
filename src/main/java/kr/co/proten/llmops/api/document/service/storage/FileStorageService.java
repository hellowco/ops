package kr.co.proten.llmops.api.document.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public File saveFile(MultipartFile file, String uploadPath) throws IOException {
        Path uploadDir = Paths.get(uploadPath);

        // 업로드 경로가 없으면 생성
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 원본 파일 이름 가져오기
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String filename = originalFilename;
        String extension = "";

        // 파일 확장자 분리
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            filename = originalFilename.substring(0, dotIndex);
            extension = originalFilename.substring(dotIndex);
        }

        // 중복 파일 이름 처리
        Path targetPath = uploadDir.resolve(originalFilename);
        while (Files.exists(targetPath)) {
            String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
            String newFilename = filename + "_" + timestamp + extension;
            targetPath = uploadDir.resolve(newFilename);
        }

        // 파일 저장
        Files.copy(file.getInputStream(), targetPath);
        log.info("File uploaded to: " + targetPath);
        return targetPath.toFile();
    }
}

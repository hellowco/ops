package kr.co.proten.llmops.api.index.service.impl;

import kr.co.proten.llmops.api.index.service.storage.FileStorageService;
import kr.co.proten.llmops.api.index.service.helper.FileValidator;
import kr.co.proten.llmops.api.index.service.IndexService;
import kr.co.proten.llmops.api.index.service.helper.TextExtractor;
import kr.co.proten.llmops.global.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IndexServiceImpl implements IndexService {

    private final FileValidator fileValidator;
    private final FileStorageService fileStorageService;
    private final TextExtractor textExtractor;

    @Value("${file.upload.path:D:/llmops/uploads}")
    private String uploadPath;

    @Value("${file.save.path:D:/llmops/saves}")
    private String savePath;

    @Autowired
    public IndexServiceImpl(FileValidator fileValidator, FileStorageService fileStorageService, TextExtractor textExtractor) {
        this.fileValidator = fileValidator;
        this.fileStorageService = fileStorageService;
        this.textExtractor = textExtractor;
    }

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 파일 검증
            fileValidator.validate(file);

            // 파일 저장
            File savedFile = fileStorageService.saveFile(file, uploadPath);

            // 텍스트 추출
            String extractedText = textExtractor.extractText(savedFile, savePath);

            // 결과 반환
            result.put("status", "success");
            result.put("message", "파일 업로드 및 텍스트 추출 성공");
            result.put("extractedText", extractedText);
        }
        catch (IOException e) {
            throw new FileStorageException("파일 저장 중 오류가 발생했습니다.", e);
        }
        return result;
    }
}

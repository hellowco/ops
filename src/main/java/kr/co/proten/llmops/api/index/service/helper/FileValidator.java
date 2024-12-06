package kr.co.proten.llmops.api.index.service.helper;

import kr.co.proten.llmops.global.exception.UnsupportedFileExtensionException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static kr.co.proten.llmops.global.common.utils.FileUtil.getExtension;

@Component
public class FileValidator {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "application/x-hwp",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document.hwp",
            "application/haansofthwp",
            "application/haansofthwpx"
    );

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "pdf",
            "docx",
            "doc",
            "ppt",
            "pptx",
            "xls",
            "xlsx",
            "txt",
            "hwp",
            "hwpx"
    );

    public void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + file.getContentType());
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileExtensionException("지원하지 않는 파일 확장자입니다: " + extension);
        }
    }


}

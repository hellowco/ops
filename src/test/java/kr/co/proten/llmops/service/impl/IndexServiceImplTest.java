package kr.co.proten.llmops.service.impl;


import kr.co.proten.llmops.api.index.service.helper.FileValidator;
import kr.co.proten.llmops.api.index.service.helper.TextExtractor;
import kr.co.proten.llmops.api.index.service.impl.IndexServiceImpl;
import kr.co.proten.llmops.api.index.service.storage.FileStorageService;
import kr.co.proten.llmops.global.exception.UnsupportedFileExtensionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IndexServiceImplTest {

    private IndexServiceImpl indexService;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private TextExtractor textExtractor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        indexService = new IndexServiceImpl(fileValidator, fileStorageService, textExtractor);

        // ReflectionTestUtils로 uploadPath 설정
        ReflectionTestUtils.setField(indexService, "uploadPath", "C:/uploads");
    }

    @AfterEach
    void cleanUpTempFiles() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        for (File file : tempDir.listFiles()) {
            if (file.getName().startsWith("test")) {
                file.delete();
            }
        }
    }

    @Test
    void testUploadFileWithPDF() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "Dummy PDF Content".getBytes()
        );

        File mockSavedFile = createTempFile("test", ".pdf", "Dummy PDF Content");
        when(fileStorageService.saveFile(mockFile, "C:/uploads")).thenReturn(mockSavedFile);
        when(textExtractor.extractText(mockSavedFile, mockSavedFile.getAbsolutePath())).thenReturn("Extracted PDF Text");

        var result = indexService.uploadFile(mockFile);

        verify(fileValidator).validate(mockFile);
        verify(fileStorageService).saveFile(mockFile, "C:/uploads");
        verify(textExtractor).extractText(mockSavedFile, mockSavedFile.getAbsolutePath());

        assertEquals("success", result.get("status"));
        assertEquals("Extracted PDF Text", result.get("extractedText"));
    }

    @Test
    void testInvalidFileExtension() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.exe", "application/octet-stream", "Invalid Content".getBytes()
        );

        doThrow(new UnsupportedFileExtensionException("지원하지 않는 파일 확장자입니다: exe"))
                .when(fileValidator).validate(invalidFile);

        Exception exception = assertThrows(
                UnsupportedFileExtensionException.class,
                () -> indexService.uploadFile(invalidFile)
        );

        assertEquals("지원하지 않는 파일 확장자입니다: exe", exception.getMessage());
    }

    private File createTempFile(String prefix, String suffix, String content) throws IOException {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), prefix + suffix);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content.getBytes());
        }
        return tempFile;
    }
}
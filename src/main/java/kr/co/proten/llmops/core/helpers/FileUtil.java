package kr.co.proten.llmops.core.helpers;

public class FileUtil {
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("올바른 파일 형식이 아닙니다: " + filename);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}

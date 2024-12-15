package kr.co.proten.llmops.core.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Date util.
 */
public class DateUtil {
    /**
     * Generate current timestamp in string.
     *
     * @return the string
     */
    public static String generateCurrentTimestamp() {
        // 현재 시간 가져오기 (서버 시간 기준)
        LocalDateTime now = LocalDateTime.now();

        // OpenSearch 날짜 형식에 맞는 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        // 포맷된 문자열 반환
        return now.format(formatter);
    }
}

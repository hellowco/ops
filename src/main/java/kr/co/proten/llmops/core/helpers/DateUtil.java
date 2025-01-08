package kr.co.proten.llmops.core.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Date util.
 */
public class DateUtil {
    /**
     * 현재시간을 생성해서 반환하는 메서드
     * 추후 시간값을 변경할 수 있는 경우, 메서드 수정
     *
     * @return LocalDateTime 현재시간 (YYYY-MM-DD HH:mm:ss 형식)
     */
    public static LocalDateTime generateCurrentTimestamp() {
        // 현재 시간 가져오기 (서버 시간 기준)
        return LocalDateTime.now();
    }

    /**
     * 현재시간을 생성해서 String으로 반환하는 메서드
     * 추후 시간값을 변경할 수 있는 경우, 메서드 수정
     *
     * @return LocalDateTime 현재시간 (YYYY-MM-DD HH:mm:ss 형식)
     */
    public static String generateCurrentTimestamp4OpenSearch() {
        // 현재 시간 가져오기 (서버 시간 기준)
        return LocalDateTime.now().toString();
    }
}

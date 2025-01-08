package kr.co.proten.llmops.core.helpers;

import java.util.UUID;

public class UUIDGenerator {
    private UUIDGenerator() {}

    /**
     * 문서 색인용: UUID 생성 메서드
     * 문서 색인시, 그냥 UUID를 생성하면 UUID 중복 현상이 있음.
     * 이를 방지하기 위해, UUID에 timestamp를 추가함.
     * @return UUID + timestamp(UTC time) as string
     */
    public static String generateUUID4Doc() {
        //UUID Version 4 생성
        UUID uuid = UUID.randomUUID();

        //UTC 타임스탬프(Epoch Time in Seconds)
        long timestamp = System.currentTimeMillis() / 1000;

        //UUID와 Timestamp 결합
        return uuid + "-" + timestamp;
    }

    /**
     * UUID4 생성하는 메서드
     * @return UUID as string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}

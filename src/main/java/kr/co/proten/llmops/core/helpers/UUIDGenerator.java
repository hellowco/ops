package kr.co.proten.llmops.core.helpers;

import java.util.UUID;

public class UUIDGenerator {
    public static String generateUUID() {
        //UUID Version 4 생성
        UUID uuid = UUID.randomUUID();

        //UTC 타임스탬프(Epoch Time in Seconds)
        long timestamp = System.currentTimeMillis() / 1000;

        //UUID와 Timestamp를 결합
        String customUUID = uuid + "-" + timestamp;

        return customUUID;
    }
}

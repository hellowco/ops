package kr.co.proten.llmops.core.data;

import lombok.Getter;

@Getter
public class ProviderData {
    private final String name;
    private final String description;
    private final String icon; // 기본 파일명

    public ProviderData(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}

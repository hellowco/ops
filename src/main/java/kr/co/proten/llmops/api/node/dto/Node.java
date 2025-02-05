package kr.co.proten.llmops.api.node.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Node {
    private final String id;
    private final String type;
    private String query;
    private Map<String, Object> input;
    private Object processData;
    private Map<String, Object> output;
}
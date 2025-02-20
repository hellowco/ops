package kr.co.proten.llmops.api.workspace.dto.request;

import com.fasterxml.jackson.databind.JsonNode;

public record CustomPatchOperation (
    String op,
    String path,
    JsonNode value
){}
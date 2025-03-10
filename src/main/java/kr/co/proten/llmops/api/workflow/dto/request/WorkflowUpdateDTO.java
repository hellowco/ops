package kr.co.proten.llmops.api.workflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record WorkflowUpdateDTO(
        @NotBlank
        @Schema(description = "워크플로우 UUID", example = "658ffad3-59a8-40dd-9623-c7302d4cc044")
        String workflow_id,

        @NotBlank
        @Schema(description = "워크플로우 그래프", example = """
                {
                  "hash": "8f14e693037397e66ae77a7a1028781782a74a1e8da587da3f5e89f8e34d5eff",
                  "graph": {
                    "edges": [
                      {
                        "id": "1735867385578-source-1735867390736-target",
                        "data": {
                          "sourceType": "start",
                          "targetType": "knowledge-retrieval",
                          "isInIteration": false
                        },
                        "type": "custom",
                        "source": "1735867385578",
                        "target": "1735867390736",
                        "zIndex": 0,
                        "sourceHandle": "source",
                        "targetHandle": "target"
                      },
                      {
                        "id": "1735867390736-source-1735867392736-target",
                        "data": {
                          "sourceType": "knowledge-retrieval",
                          "targetType": "llm",
                          "isInIteration": false
                        },
                        "type": "custom",
                        "source": "1735867390736",
                        "target": "1735867392736",
                        "zIndex": 0,
                        "sourceHandle": "source",
                        "targetHandle": "target"
                      },
                      {
                        "id": "1735867392736-source-1735867395193-target",
                        "data": {
                          "sourceType": "llm",
                          "targetType": "end",
                          "isInIteration": false
                        },
                        "type": "custom",
                        "source": "1735867392736",
                        "target": "1735867395193",
                        "zIndex": 0,
                        "sourceHandle": "source",
                        "targetHandle": "target"
                      }
                    ],
                    "nodes": [
                      {
                        "id": "1735867385578",
                        "data": {
                          "desc": "",
                          "type": "start",
                          "query": "test",
                          "title": "시작",
                          "selected": false
                        },
                        "type": "custom",
                        "width": 243,
                        "height": 115,
                        "position": {
                          "x": 80,
                          "y": 282
                        },
                        "selected": false,
                        "sourcePosition": "right",
                        "targetPosition": "left",
                        "positionAbsolute": {
                          "x": 80,
                          "y": 282
                        }
                      },
                      {
                        "id": "1735867390736",
                        "data": {
                          "desc": "",
                          "type": "knowledge-retrieval",
                          "title": "지식 검색",
                          "datasets": [
                            {
                              "k": "3",
                              "page": "1",
                              "query": "{{#1735867385578.query#}}",
                              "page_size": "10",
                              "model_name": "llmops",
                              "model_type": "ProsLLM",
                              "search_type": "hybrid",
                              "vector_weight": "0.5",
                              "keyword_weight": "0.5",
                              "knowledge_name": "test"
                            }
                          ],
                          "selected": false
                        },
                        "type": "custom",
                        "width": 243,
                        "height": 91,
                        "position": {
                          "x": 402,
                          "y": 283
                        },
                        "selected": false,
                        "sourcePosition": "right",
                        "targetPosition": "left",
                        "positionAbsolute": {
                          "x": 402,
                          "y": 283
                        }
                      },
                      {
                        "id": "1735867392736",
                        "data": {
                          "desc": "",
                          "type": "llm",
                          "title": "LLM",
                          "selected": false,
                          "llm_settings": {
                            "model": {
                              "mode": "chat",
                              "name": "deepseek-r1:8b",
                              "provider": "ollama",
                              "completion_params": {
                                "top_k": 1,
                                "top_p": 0.9,
                                "num_ctx": 2048,
                                "mirostat": 0,
                                "num_predict": 512,
                                "temperature": 0.7,
                                "mirostat_eta": 0,
                                "mirostat_tau": 0,
                                "repeat_penalty": -2
                              }
                            },
                            "vision": {
                              "enabled": false
                            },
                            "context": ["{{#1735867390736.results#}}","{{#1735867390736.results#}}"],
                            "prompt_template": [
                              {
                                "id": "64d95237-4e13-4ea5-a544-641b1ea55455",
                                "role": "system",
                                "text": "사용자의 질문에 성심껏 답해."
                              },
                              {
                                "id": "246aa6f6-2dd7-4676-85ee-6befe188641c",
                                "role": "user",
                                "text": "아래 내용을 보고 질문에 답해줘\\n{{#1735867385578.query#}}\\n{{#context#}}"
                              }
                            ]
                          }
                        },
                        "type": "custom",
                        "width": 243,
                        "height": 97,
                        "position": {
                          "x": 680,
                          "y": 282
                        },
                        "selected": false,
                        "sourcePosition": "right",
                        "targetPosition": "left",
                        "positionAbsolute": {
                          "x": 680,
                          "y": 282
                        }
                      },
                      {
                        "id": "1735867395193",
                        "data": {
                          "desc": "",
                          "type": "end",
                          "title": "끝",
                          "selected": true,
                          "variables": ["{{#1735867392736.text#}}"]
                        },
                        "type": "custom",
                        "width": 243,
                        "height": 89,
                        "position": {
                          "x": 969,
                          "y": 287
                        },
                        "selected": true,
                        "sourcePosition": "right",
                        "targetPosition": "left",
                        "positionAbsolute": {
                          "x": 969,
                          "y": 287
                        }
                      }
                    ],
                    "viewport": {
                      "x": -46,
                      "y": 76,
                      "zoom": 1
                    }
                  }
                }""")
        String workflow_data
) {
}

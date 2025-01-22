package kr.co.proten.llmops.api.workflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record WorkflowUpdateDTO(
        @NotBlank
        @Schema(description = "워크플로우 UUID", example = "658ffad3-59a8-40dd-9623-c7302d4cc044")
        String workflow_id,

        @NotBlank
        @Schema(description = "워크플로우 그래프", example = "{\"hash\": \"8f14e693037397e66ae77a7a1028781782a74a1e8da587da3f5e89f8e34d5eff\", \"graph\": {\"edges\": [{\"id\": \"1735867385578-source-1735867390736-target\", \"data\": {\"sourceType\": \"start\", \"targetType\": \"knowledge-retrieval\", \"isInIteration\": false}, \"type\": \"custom\", \"source\": \"1735867385578\", \"target\": \"1735867390736\", \"zIndex\": 0, \"sourceHandle\": \"source\", \"targetHandle\": \"target\"}, {\"id\": \"1735867390736-source-1735867392736-target\", \"data\": {\"sourceType\": \"knowledge-retrieval\", \"targetType\": \"llm\", \"isInIteration\": false}, \"type\": \"custom\", \"source\": \"1735867390736\", \"target\": \"1735867392736\", \"zIndex\": 0, \"sourceHandle\": \"source\", \"targetHandle\": \"target\"}, {\"id\": \"1735867392736-source-1735867395193-target\", \"data\": {\"sourceType\": \"llm\", \"targetType\": \"end\", \"isInIteration\": false}, \"type\": \"custom\", \"source\": \"1735867392736\", \"target\": \"1735867395193\", \"zIndex\": 0, \"sourceHandle\": \"source\", \"targetHandle\": \"target\"}], \"nodes\": [{\"id\": \"1735867385578\", \"data\": {\"desc\": \"\", \"type\": \"start\", \"title\": \"시작\", \"selected\": false, \"variables\": [{\"type\": \"text-input\", \"label\": \"queryLabel\", \"options\": [], \"required\": true, \"variable\": \"query\", \"max_length\": 48}, {\"type\": \"text-input\", \"label\": \"systemPrompt\", \"options\": [], \"required\": false, \"variable\": \"systemPrompt\", \"max_length\": 48}, {\"type\": \"select\", \"label\": \"select\", \"options\": [\"1\", \"2\", \"3\"], \"required\": true, \"variable\": \"select\", \"max_length\": 48}]}, \"type\": \"custom\", \"width\": 243, \"height\": 115, \"position\": {\"x\": 80, \"y\": 282}, \"selected\": false, \"sourcePosition\": \"right\", \"targetPosition\": \"left\", \"positionAbsolute\": {\"x\": 80, \"y\": 282}}, {\"id\": \"1735867390736\", \"data\": {\"desc\": \"\", \"type\": \"knowledge-retrieval\", \"title\": \"지식 검색\", \"selected\": false, \"dataset_ids\": [\"c2b08cf6-fae1-464d-b681-c17d8a6c5c7d\"], \"retrieval_mode\": \"multiple\", \"query_variable_selector\": [\"1735867385578\", \"query\"], \"multiple_retrieval_config\": {\"top_k\": 4, \"reranking_mode\": \"reranking_model\", \"reranking_enable\": false}}, \"type\": \"custom\", \"width\": 243, \"height\": 91, \"position\": {\"x\": 402, \"y\": 283}, \"selected\": false, \"sourcePosition\": \"right\", \"targetPosition\": \"left\", \"positionAbsolute\": {\"x\": 402, \"y\": 283}}, {\"id\": \"1735867392736\", \"data\": {\"desc\": \"\", \"type\": \"llm\", \"model\": {\"mode\": \"chat\", \"name\": \"llama3.1:8b\", \"provider\": \"ollama\", \"completion_params\": {\"top_k\": 1, \"top_p\": 0.9, \"num_ctx\": 2048, \"mirostat\": 0, \"num_predict\": 512, \"temperature\": 0.7, \"mirostat_eta\": 0, \"mirostat_tau\": 0, \"repeat_penalty\": -2}}, \"title\": \"LLM\", \"vision\": {\"enabled\": false}, \"context\": {\"enabled\": true, \"variable_selector\": [\"1735867390736\", \"result\"]}, \"selected\": false, \"variables\": [], \"prompt_template\": [{\"id\": \"64d95237-4e13-4ea5-a544-641b1ea55455\", \"role\": \"system\", \"text\": \"{{#1735867385578.systemPrompt#}}\"}, {\"id\": \"246aa6f6-2dd7-4676-85ee-6befe188641c\", \"role\": \"user\", \"text\": \"아래 내용을 보고 질문에 답해줘\\n{{#1735867385578.query#}}\\n{{#context#}}\"}]}, \"type\": \"custom\", \"width\": 243, \"height\": 97, \"position\": {\"x\": 680, \"y\": 282}, \"selected\": false, \"sourcePosition\": \"right\", \"targetPosition\": \"left\", \"positionAbsolute\": {\"x\": 680, \"y\": 282}}, {\"id\": \"1735867395193\", \"data\": {\"desc\": \"\", \"type\": \"end\", \"title\": \"끝\", \"outputs\": [{\"variable\": \"text\", \"value_selector\": [\"1735867392736\", \"text\"]}], \"selected\": true}, \"type\": \"custom\", \"width\": 243, \"height\": 89, \"position\": {\"x\": 969, \"y\": 287}, \"selected\": true, \"sourcePosition\": \"right\", \"targetPosition\": \"left\", \"positionAbsolute\": {\"x\": 969, \"y\": 287}}], \"viewport\": {\"x\": -46, \"y\": 76, \"zoom\": 1}}, \"features\": {\"file_upload\": {\"image\": {\"enabled\": false, \"number_limits\": 3, \"transfer_methods\": [\"local_file\", \"remote_url\"]}, \"enabled\": false, \"number_limits\": 3, \"fileUploadConfig\": {\"file_size_limit\": 15, \"batch_count_limit\": 5, \"audio_file_size_limit\": 50, \"image_file_size_limit\": 10, \"video_file_size_limit\": 100, \"workflow_file_upload_limit\": 10}, \"allowed_file_types\": [\"image\"], \"allowed_file_extensions\": [\".JPG\", \".JPEG\", \".PNG\", \".GIF\", \".WEBP\", \".SVG\"], \"allowed_file_upload_methods\": [\"local_file\", \"remote_url\"]}, \"speech_to_text\": {\"enabled\": false}, \"text_to_speech\": {\"voice\": \"\", \"enabled\": false, \"language\": \"\"}, \"opening_statement\": \"\", \"retriever_resource\": {\"enabled\": true}, \"suggested_questions\": [], \"sensitive_word_avoidance\": {\"enabled\": false}, \"suggested_questions_after_answer\": {\"enabled\": false}}, \"environment_variables\": [], \"conversation_variables\": []}")
        String graph
) {
}

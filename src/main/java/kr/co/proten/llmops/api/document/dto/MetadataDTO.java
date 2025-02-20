package kr.co.proten.llmops.api.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.proten.llmops.api.document.entity.Metadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MetadataDTO {

    @Schema(description = "고유 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
    private String id;

    @Schema(description = "문서 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
    private String docId;

    @Schema(description = "지식명", example = "솔루션사업부")
    private String knowledgeName;

    @Schema(description = "문서 설명", example = "text파일을 pdf로 테스트 문서")
    private String description;

    @Schema(description = "문서 활성 상태 여부", example = "true")
    private String isActive;

    @Schema(description = "문서 마지막 수정일", example = "yyyy-MM-dd HH:mm:ss")
    private String lastUpdatedDate;

    @Schema(description = "문서가 변환된 날짜", example = "yyyy-MM-dd HH:mm:ss")
    private String convertDate;

    @Schema(description = "문서 기존 이름", example = "txt2pdf.txt")
    private String orgFileName;

    @Schema(description = "지식 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
    private String orgFilePath;

    @Schema(description = "문서 총 페이지 수", example = "22")
    private long totalPage;

    @Schema(description = "문서를 나눈 청크 크기", example = "200")
    private long chunkSize;

    @Schema(description = "문서가 청크된 수", example = "4")
    private long chunkNum;

    @Schema(description = "PDF로 변환된 파일 이름", example = "txt2pdf.pdf")
    private String pdfFileName;

    @Schema(description = "PDF로 변환된 파일 경로", example = "/home/user/pdf")
    private String pdfFilePath;

    @Schema(description = "유저 ID", example = "test_user")
    private String userId;

    @Schema(description = "버전정보", example = "v1.0")
    private String version;

    // DTO -> Entity
    public static Metadata toMetadata(MetadataDTO dto) {
        return Metadata.builder()
                .id(dto.getId())
                .docId(dto.getDocId())
                .knowledgeName(dto.getKnowledgeName())
                .description(dto.getDescription())
                .isActive(Boolean.parseBoolean(dto.getIsActive()))
                .lastUpdatedDate(dto.getLastUpdatedDate())
                .convertDate(dto.getConvertDate())
                .orgFileName(dto.getOrgFileName())
                .orgFilePath(dto.getOrgFilePath())
                .totalPage(dto.getTotalPage())
                .chunkSize(dto.getChunkSize())
                .chunkNum(dto.getChunkNum())
                .pdfFileName(dto.getPdfFileName())
                .pdfFilePath(dto.getPdfFilePath())
                .userId(dto.getUserId())
                .version(dto.getVersion())
                .build();
    }

    // Entity -> DTO
    public static MetadataDTO of(Metadata entity) {
        return MetadataDTO.builder()
                .id(entity.getId())
                .docId(entity.getDocId())
                .knowledgeName(entity.getKnowledgeName())
                .description(entity.getDescription())
                .isActive(String.valueOf(entity.isActive()))
                .lastUpdatedDate(entity.getLastUpdatedDate())
                .convertDate(entity.getConvertDate())
                .orgFileName(entity.getOrgFileName())
                .orgFilePath(entity.getOrgFilePath())
                .totalPage(entity.getTotalPage())
                .chunkSize(entity.getChunkSize())
                .chunkNum(entity.getChunkNum())
                .pdfFileName(entity.getPdfFileName())
                .pdfFilePath(entity.getPdfFilePath())
                .userId(entity.getUserId())
                .version(entity.getVersion())
                .build();
    }

}

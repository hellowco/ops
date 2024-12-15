package kr.co.proten.llmops.api.document.dto;

import kr.co.proten.llmops.api.document.entity.Metadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MetadataDTO {
    private String id;
    private String docId;
    private String index;
    private String isActive;
    private String lastUpdatedDate;
    private String convertDate;
    private String orgFileName;
    private String orgFilePath;
    private long totalPage;
    private long chunkSize;
    private long chunkNum;
    private String pdfFileName;
    private String pdfFilePath;
    private String userId;
    private String version;

    // DTO -> Entity
    public static Metadata toMetadata(MetadataDTO dto) {
        return Metadata.builder()
                .id(dto.getId())
                .docId(dto.getDocId())
                .index(dto.getIndex())
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
                .index(entity.getIndex())
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

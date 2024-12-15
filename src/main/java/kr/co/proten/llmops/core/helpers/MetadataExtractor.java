//package kr.co.proten.llmops.core.helpers;
//
//public class MetadataExtractor {
//
//    public DocumentMetadata extract(MultipartFile file) {
//        String fileType = file.getContentType();
//        if (fileType.equals("application/pdf")) {
//            return extractPdfMetadata(file);
//        } else if (fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
//            return extractDocxMetadata(file);
//        }
//        throw new UnsupportedFileTypeException("Unsupported file type: " + fileType);
//    }
//
//    private DocumentMetadata extractPdfMetadata(MultipartFile file) {
//        // PDFBox 또는 iText 라이브러리로 PDF 메타데이터 추출
//        PDDocument pdf = PDDocument.load(file.getInputStream());
//        PDDocumentInformation info = pdf.getDocumentInformation();
//
//        return new DocumentMetadata(
//                info.getTitle(),
//                info.getAuthor(),
//                info.getCreationDate().getTime(),
//                file.getOriginalFilename()
//        );
//    }
//
//    private DocumentMetadata extractDocxMetadata(MultipartFile file) {
//        // Apache POI 라이브러리로 DOCX 메타데이터 추출
//        XWPFDocument docx = new XWPFDocument(file.getInputStream());
//        POIXMLProperties.CoreProperties props = docx.getProperties().getCoreProperties();
//
//        return new DocumentMetadata(
//                props.getTitle(),
//                props.getCreator(),
//                props.getCreated().getTime(),
//                file.getOriginalFilename()
//        );
//    }
//}

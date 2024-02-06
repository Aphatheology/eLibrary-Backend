package com.aphatheology.elibrarybackend.service;

import com.aphatheology.elibrarybackend.entity.FilesData;
import com.aphatheology.elibrarybackend.exception.ResourceNotFoundException;
import com.aphatheology.elibrarybackend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${app.env}")
    private String environment;

    public static boolean isOnProduction(String env) {
        return env.trim().equals("production");
    }

    public String uploadFile(MultipartFile file, String url) throws IOException {
        if (file.isEmpty()) throw new ResourceNotFoundException("File cannot be empty");
        String baseUrl = url.substring(0, url.lastIndexOf("files"));
        FilesData fileData = uploadFileData(file);
        String fileUrl = String.format("%s%s%s", baseUrl, "files/", fileData.getId()) + "." + fileData.getType();
        return formatUrl(fileUrl);
    }

    public String formatUrl(String fileUrl) {
        if (isOnProduction(environment))
            return fileUrl.replaceFirst("http", "https");
        return fileUrl;
    }

    private FilesData uploadFileData(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String fileType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        byte[] compressedFile = compressFile(file.getBytes());
        InputStream is = new ByteArrayInputStream(compressedFile);
        FilesData fileData = new FilesData(originalFileName, fileType, is.readAllBytes());
        return fileRepository.save(fileData);
    }

    public FilesData getFile(String fileId) throws IOException {
        FilesData dbFile = fileRepository.findById(fileId).orElse(null);
        if (dbFile != null) {
            byte[] compressedFile = dbFile.getBytes();
            byte[] decompressedFile = decompressFile(compressedFile);
            dbFile.setBytes(decompressedFile);
        }
        return dbFile;
    }

//    public FilesData getFileWithId(Long fileId) throws IOException {
//        FilesData dbFile = fileRepository.findById(fileId).orElse(null);
//        if (dbFile != null) {
//            byte[] compressedFile = dbFile.getBytes();
//            byte[] decompressedFile = decompressFile(compressedFile);
//            dbFile.setBytes(decompressedFile);
//        }
//        return dbFile;
//    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        is.close();
        return baos.toByteArray();
    }

    private byte[] compressFile(byte[] fileData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        gzipOut.write(fileData);
        gzipOut.close();
        return baos.toByteArray();
    }

    private byte[] decompressFile(byte[] compressedFile) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedFile);
        GZIPInputStream gzipIn = new GZIPInputStream(bais);
        return getBytes(gzipIn);
    }

    public MediaType getMediaTypeForFileType(String fileType) {
        return switch (fileType) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "png" -> MediaType.IMAGE_PNG;
            case "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "csv", "txt" -> MediaType.TEXT_PLAIN;
            case "xml" -> MediaType.APPLICATION_XML;
            case "json" -> MediaType.APPLICATION_JSON;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public void deleteFile(String fileId) {
        fileRepository.findById(fileId)
                .ifPresent(fileRepository::delete);
    }

}

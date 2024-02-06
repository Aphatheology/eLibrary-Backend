package com.aphatheology.elibrarybackend.controller;

import com.aphatheology.elibrarybackend.dto.ApiResponse;
import com.aphatheology.elibrarybackend.entity.FilesData;
import com.aphatheology.elibrarybackend.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable("fileId") String fileId) {
        try {
            FilesData file = fileService.getFile(fileId.contains(".") ? fileId.substring(0, fileId.lastIndexOf(".")) : fileId);
            byte[] fileContent = file.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(fileService.getMediaTypeForFileType(file.getType()));
            headers.setContentLength(fileContent.length);
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(file.getName()).build());
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));
        }
    }

//    @GetMapping("/id/{fileId}")
//    public ResponseEntity<?> getFileWithId(@PathVariable("fileId") Long fileId) {
//        try {
//            FilesData file = fileService.getFileWithId(fileId);
//            byte[] fileContent = file.getBytes();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(fileService.getMediaTypeForFileType(file.getType()));
//            headers.setContentLength(fileContent.length);
//            headers.setContentDisposition(ContentDisposition.builder("inline").filename(file.getName()).build());
//            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));
//            return ResponseEntity.ok().headers(headers).body(resource);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));
//        }
//    }

//    @GetMapping("/download/id/{fileId}")
//    public ResponseEntity<?> downloadFileWithId(@PathVariable("fileId") Long fileId) {
//        try {
//            FilesData filesData = fileService.getFileWithId(fileId);
//            ByteArrayResource resource = new ByteArrayResource(filesData.getBytes());
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filesData.getName());
//            headers.setContentType(fileService.getMediaTypeForFileType(filesData.getType()));
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(resource);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));
//        }
//    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileId") String fileId) {
        try {
            FilesData filesData = fileService.getFile(fileId.contains(".") ? fileId.substring(0, fileId.lastIndexOf(".")) : fileId);
            ByteArrayResource resource = new ByteArrayResource(filesData.getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filesData.getName());
            headers.setContentType(fileService.getMediaTypeForFileType(filesData.getType()));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        try {
            return ResponseEntity.ok(new ApiResponse(true, "File successfully uploaded",
                    fileService.uploadFile(file, url)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable("fileId") String fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(new ApiResponse(true, "File deleted successfully"));
    }
}

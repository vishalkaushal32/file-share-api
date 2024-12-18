package com.fileshare.Controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.fileshare.Dto.FileDto;
import com.fileshare.Dto.Request.FileUploadRequestDto;
import com.fileshare.Dto.Response.FileUploadResponseDto;
import com.fileshare.Model.File;
import com.fileshare.Service.FileService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "ttl", required = false) String ttl) throws IOException {
        // Parse TTL or set a default value (e.g., 1 day from now)
        FileUploadRequestDto requestDto = new FileUploadRequestDto();
        requestDto.setFiles(files);
        requestDto.setTtl(ttl != null ? LocalDateTime.parse(ttl) : LocalDateTime.now().plusDays(1));

        // Call the service
        FileUploadResponseDto responseDto = fileService.uploadFiles(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("passphrase") String passphrase) {

        // Fetch the files based on the transaction ID and passphrase
        List<FileDto> files = fileService.getFiles(passphrase);

        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("No files found for download passphrase: " + passphrase).getBytes());
        }

        if (files.size() == 1) {
            // Single file download
            FileDto file = files.get(0);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getFileType())
                    .body(Base64.getDecoder().decode(file.getFileData()));
        } else {
            // Multiple files: create a ZIP archive
            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();

            try (ZipOutputStream zipStream = new ZipOutputStream(zipOutputStream)) {
                for (FileDto file : files) {
                    ZipEntry zipEntry = new ZipEntry(file.getFileName());
                    zipStream.putNextEntry(zipEntry);
                    zipStream.write(Base64.getDecoder().decode(file.getFileData()));
                    zipStream.closeEntry();
                }
                zipStream.finish();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error creating ZIP file: " + e.getMessage()).getBytes());
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                    .body(zipOutputStream.toByteArray());
        }
    }

}
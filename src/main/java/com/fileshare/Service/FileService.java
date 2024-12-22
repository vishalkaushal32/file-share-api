package com.fileshare.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils; // Add this dependency
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fileshare.Dto.FileDto;
import com.fileshare.Dto.Request.FileUploadRequestDto;
import com.fileshare.Dto.Response.FileUploadResponseDto;
import com.fileshare.Model.File;
import com.fileshare.Repository.FileRepository;
import com.fileshare.config.AppProperties;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private AppProperties appProps;

    public FileUploadResponseDto uploadFiles(FileUploadRequestDto requestDto) throws IOException {
        List<MultipartFile> files = requestDto.getFiles();
        LocalDateTime ttl = requestDto.getTtl();

        // Validate constraints
        if (files.size() > appProps.getMaxFileCount()) {
            throw new IllegalArgumentException("You can upload a maximum of 5 files at once.");
        }

        List<String> fileNames = new ArrayList<>();
        String transactionId = UUID.randomUUID().toString();

        // Generate a unique 6-character alphanumeric passphrase
        String downloadPassphrase = RandomStringUtils.randomAlphanumeric(6);

        // Process each file
        for (MultipartFile file : files) {
            if (file.getSize() > appProps.getMaxFileSizeBytes()) {
                throw new IllegalArgumentException("File size cannot exceed 5 MB.");
            }

            // Convert file to Base64
            String base64Content = Base64.getEncoder().encodeToString(file.getBytes());

            // Save file to DB
            File dbFile = new File();
            dbFile.setTransactionId(transactionId);
            dbFile.setFileName(file.getOriginalFilename());
            dbFile.setFileType(file.getContentType());
            dbFile.setFileSize((int) file.getSize());
            dbFile.setTtl(ttl);
            dbFile.setDownloadPassphrase(downloadPassphrase); // Store the passphrase
            dbFile.setFileUri(base64Content);

            fileRepository.save(dbFile);
            fileNames.add(file.getOriginalFilename());
        }

        // Create response
        FileUploadResponseDto responseDto = new FileUploadResponseDto();
        responseDto.setTransactionId(transactionId);
        responseDto.setFileNames(fileNames);
        responseDto.setDownloadPassphrase(downloadPassphrase); // Include passphrase in the response

        return responseDto;
    }

    // public List<File> downloadFiles(String transactionId, String passphrase) {
    // return fileRepository.findByTransactionIdAndDownloadPassphrase(transactionId,
    // passphrase)
    // .orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID or
    // passphrase."));
    // }

    public List<FileDto> getFiles(String passphrase) {
        List<File> fileEntities = fileRepository.findByDownloadPassphrase(passphrase);

        return fileEntities.stream().map(this::toDto).toList();
    }

    private FileDto toDto(File entity) {
        FileDto dto = new FileDto();
        dto.setFileName(entity.getFileName());
        dto.setFileType(entity.getFileType());
        dto.setFileData(entity.getFileUri());
        return dto;
    }
}

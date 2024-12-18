package com.fileshare.Dto.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FileUploadRequestDto {
    private List<MultipartFile> files;  // Files being uploaded
    private LocalDateTime ttl;  // Time-to-Live for the files
}


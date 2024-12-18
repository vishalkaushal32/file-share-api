package com.fileshare.Dto;

import lombok.Data;

@Data
public class FileDto {

    private String fileName;    // File name (e.g., "report.pdf")
    private String fileType;    // MIME type (e.g., "application/pdf")
    private String fileData;    // Base64-encoded file content

    // Getters and Setters
}

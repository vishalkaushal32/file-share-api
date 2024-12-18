package com.fileshare.Dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class FileUploadResponseDto {
    private String transactionId;
    private List<String> fileNames;
    private String downloadPassphrase;  // Added passphrase field
}
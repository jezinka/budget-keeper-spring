package com.example.budgetkeeperspring.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileService {

    public static File getTempFile(MultipartFile file) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("upload_", "_" + file.getOriginalFilename());
            tempFile.toFile().deleteOnExit();
            Files.write(tempFile, file.getBytes());
            System.out.println("File created successfully: " + tempFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to create file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return tempFile.toFile();
    }
}

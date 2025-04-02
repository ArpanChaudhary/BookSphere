package com.booksphere.service.impl;

import com.booksphere.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementation of FileService interface for handling file operations.
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file, String directory) {
        try {
            // Create the target directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, directory).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate a unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Save the file
            Path targetLocation = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative URL
            return "/uploads/" + directory + "/" + newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Convert URL to file path
            String filePath = fileUrl.replace("/uploads/", "");
            Path fullPath = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();

            // Delete the file if it exists
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file. Please try again!", ex);
        }
    }
} 
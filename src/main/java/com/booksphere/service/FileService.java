package com.booksphere.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for handling file operations.
 */
public interface FileService {
    
    /**
     * Save a file to the specified directory.
     * 
     * @param file The file to save
     * @param directory The directory to save the file in
     * @return The URL of the saved file
     */
    String saveFile(MultipartFile file, String directory);
    
    /**
     * Delete a file by its URL.
     * 
     * @param fileUrl The URL of the file to delete
     */
    void deleteFile(String fileUrl);
} 
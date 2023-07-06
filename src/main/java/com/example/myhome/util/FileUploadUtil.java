package com.example.myhome.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@Data
@RequiredArgsConstructor
@Log
public class FileUploadUtil {

    /*

    Класс для сохранения файлов от клиента

     */

    @Value("${upload.path}")
    private String uploadPath;

    public void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {

        log.info(this.uploadPath);

        log.info("File name!");
        log.info(fileName);

        Path path = Paths.get(uploadPath + uploadDir);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = path.resolve(fileName);
            log.info("Path for saving the image: " + filePath);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }
}

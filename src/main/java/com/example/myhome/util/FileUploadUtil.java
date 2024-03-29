package com.example.myhome.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileUploadUtil {

    /*

    Класс для сохранения файлов от клиента

     */

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.path.file}")
    private String uploadPathFile;

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
            throw new IOException("Could not save image: " + fileName, ioe);
        }
    }

    public void saveFile2(String uploadDir, String fileName, MultipartFile file) throws IOException {

        log.info(this.uploadPathFile);

        log.info("File name!");
        log.info(fileName);

        Path path = Paths.get(uploadPathFile + uploadDir);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = path.resolve(fileName);
            log.info("Path for saving the file: " + filePath);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }
    }
}

package com.example.myhome.util;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Log
public class FileDownloadUtil {

    /*

    Класс для загрузки свежесозданных квитанций (или других файлов) из браузера

    Временный файл квитанции копируется в проект -> файл предлагается на загрузку клиенту ->
    -> файл загружается -> временный файл удаляется с места дислокации

     */

    private static final String FILE_PATH = "C:\\Users\\OneSmiLe\\IdeaProjects\\MyHome\\src\\main\\resources\\static\\files\\";

    private FileDownloadUtil() {
    }

    public void downloadInvoice(HttpServletResponse response, String fileName) throws IOException {

        downloadFile(response, fileName);

        File file = new File(FILE_PATH + fileName);
        if(file.exists()) {
            boolean delete = file.delete();
            if(delete) log.info("File successfully deleted");
            else log.info("Something went wrong with deletion");
        }
    }

    public void downloadFile(HttpServletResponse response, String fileName) throws IOException {
        File file = new File(FILE_PATH + fileName);
        if(file.exists()) {
            log.info("File found!");
            String file_extension = "." + file.getName().split("\\.")[1];

            //mime type
            String mime = URLConnection.guessContentTypeFromName(fileName);
            if(mime == null) mime = "application/octet-stream";

            response.setContentType(mime);
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
            response.setHeader("Content-Disposition", "attachment; filename=\"file-"+ date + file_extension +"\"");

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
        else {
            log.severe("File not found!");
            throw new FileNotFoundException("File not found");
        }
    }

}

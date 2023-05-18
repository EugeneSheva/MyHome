package com.example.myhome.validator;

import com.example.myhome.model.pages.MainPage;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Component
@Log
public class MainPageValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return MainPage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MainPage page = (MainPage) target;
        MultipartFile img1 = page.getImg1();

        if(img1 != null && img1.getSize() > 0) {
            log.info("Checking img1...");
            try {
                InputStream stream = new ByteArrayInputStream(img1.getBytes());
                Dimension dimension = getImageDimensions(stream);
                log.info("Height: " + dimension.getHeight());
                log.info("Width: " + dimension.getWidth());

                if(dimension.getWidth() != 1920 || dimension.getHeight() != 800) {
                    errors.rejectValue("img1", "img1.wrong_dimensions", "Неправильный размер картинки!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Dimension getImageDimensions(Object input) throws IOException {

        try (ImageInputStream stream = ImageIO.createImageInputStream(input)) { // accepts File, InputStream, RandomAccessFile
            if(stream != null) {
                IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
                Iterator<ImageReaderSpi> iter = iioRegistry.getServiceProviders(ImageReaderSpi.class, true);
                while (iter.hasNext()) {
                    ImageReaderSpi readerSpi = iter.next();
                    if (readerSpi.canDecodeInput(stream)) {
                        ImageReader reader = readerSpi.createReaderInstance();
                        try {
                            reader.setInput(stream);
                            int width = reader.getWidth(reader.getMinIndex());
                            int height = reader.getHeight(reader.getMinIndex());
                            return new Dimension(width, height);
                        } finally {
                            reader.dispose();
                        }
                    }
                }
                throw new IllegalArgumentException("Can't find decoder for this image");
            } else {
                throw new IllegalArgumentException("Can't open stream for this image");
            }
        }
    }
}

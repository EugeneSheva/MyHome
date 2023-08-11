package com.example.myhome.validator;

import com.example.myhome.model.pages.MainPage;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
public class MainPageValidator implements Validator {

    public static List<String> contentTypes = List.of("image/png", "image/jpeg", "image/jpg");

    @Override
    public boolean supports(Class<?> clazz) {
        return MainPage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MainPage page = (MainPage) target;
        MultipartFile img1 = page.getImg1();

        String seo_title = page.getSeo_title();
        if(seo_title == null || seo_title.isEmpty()) {
            errors.rejectValue("seo_title", "seo_title.empty", "Title can't be empty");
        } else if(seo_title.length() < 2 || seo_title.length() > 100) {
            errors.rejectValue("seo_title", "seo_title.length", "Title length: 2-200");
        }

        String seo_desc = page.getSeo_description();
        if(seo_desc == null || seo_desc.isEmpty()) {
            errors.rejectValue("seo_description", "seo_desc.empty", "Desc can't be empty");
        } else if(seo_desc.length() < 2 || seo_desc.length() > 250) {
            errors.rejectValue("seo_description", "seo_desc.length", "Desc length: 2-250");
        }

        String seo_keywords = page.getSeo_keywords();
        if(seo_keywords == null || seo_keywords.isEmpty()) {
            errors.rejectValue("seo_keywords", "seo_keywords.empty", "Keywords can't be empty");
        } else if(seo_keywords.length() < 2 || seo_keywords.length() > 200) {
            errors.rejectValue("seo_keywords", "seo_keywords.length", "Keywords length: 2-200");
        }
//
//        if(img1 != null && img1.getSize() > 0) {
//            log.info("Checking img1...");
//            try {
//                InputStream stream = new ByteArrayInputStream(img1.getBytes());
//                Dimension dimension = getImageDimensions(stream);
//                log.info("Height: " + dimension.getHeight());
//                log.info("Width: " + dimension.getWidth());
//
//                if(dimension.getWidth() != 1920 || dimension.getHeight() != 800) {
//                    errors.rejectValue("img1", "img1.wrong_dimensions", "Неправильный размер картинки!");
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

//    private static Dimension getImageDimensions(Object input) throws IOException {
//
//        try (ImageInputStream stream = ImageIO.createImageInputStream(input)) { // accepts File, InputStream, RandomAccessFile
//            if(stream != null) {
//                IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
//                Iterator<ImageReaderSpi> iter = iioRegistry.getServiceProviders(ImageReaderSpi.class, true);
//                while (iter.hasNext()) {
//                    ImageReaderSpi readerSpi = iter.next();
//                    if (readerSpi.canDecodeInput(stream)) {
//                        ImageReader reader = readerSpi.createReaderInstance();
//                        try {
//                            reader.setInput(stream);
//                            int width = reader.getWidth(reader.getMinIndex());
//                            int height = reader.getHeight(reader.getMinIndex());
//                            return new Dimension(width, height);
//                        } finally {
//                            reader.dispose();
//                        }
//                    }
//                }
//                throw new IllegalArgumentException("Can't find decoder for this image");
//            } else {
//                throw new IllegalArgumentException("Can't open stream for this image");
//            }
//        }
//    }

    public void validateMainPage(Object target, Errors e,MultipartFile page_slide1,
                                MultipartFile page_slide2,
                                MultipartFile page_slide3,
                                MultipartFile page_block_1_img,
                                MultipartFile page_block_2_img,
                                MultipartFile page_block_3_img,
                                MultipartFile page_block_4_img,
                                MultipartFile page_block_5_img,
                                MultipartFile page_block_6_img) {
        MainPage page = (MainPage) target;
        if(page_slide1 != null && page_slide1.getSize() > 0 && !contentTypes.contains(page_slide1.getContentType())) {
            e.rejectValue("slide1", ".", "Неправильное расширение файла!");
        }
        if(page_slide1 != null && page_slide1.getSize() > 19_999_999) {
            e.rejectValue("slide1", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_slide2 != null && page_slide2.getSize() > 0 && !contentTypes.contains(page_slide2.getContentType())) {
            e.rejectValue("slide2", ".", "Неправильное расширение файла!");
        }
        if(page_slide2 != null && page_slide2.getSize() > 19_999_999) {
            e.rejectValue("slide2", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_slide3 != null && page_slide3.getSize() > 0 && !contentTypes.contains(page_slide3.getContentType())) {
            e.rejectValue("slide3", ".", "Неправильное расширение файла!");
        }
        if(page_slide3 != null && page_slide3.getSize() > 19_999_999) {
            e.rejectValue("slide3", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_block_1_img != null && page_block_1_img.getSize() > 0 && !contentTypes.contains(page_block_1_img.getContentType())) {
            e.rejectValue("block_1_img", ".", "Неправильное расширение файла!");
        }
        if(page_block_1_img != null && page_block_1_img.getSize() > 19_999_999) {
            e.rejectValue("block_1_img", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_block_2_img != null && page_block_2_img.getSize() > 0 && !contentTypes.contains(page_block_2_img.getContentType())) {
            e.rejectValue("block_2_img", ".", "Неправильное расширение файла!");
        }
        if(page_block_2_img != null && page_block_2_img.getSize() > 19_999_999) {
            e.rejectValue("block_2_img", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_block_3_img != null && page_block_3_img.getSize() > 0 && !contentTypes.contains(page_block_3_img.getContentType())) {
            e.rejectValue("block_3_img", ".", "Неправильное расширение файла!");
        }
        if(page_block_3_img != null && page_block_3_img.getSize() > 19_999_999) {
            e.rejectValue("block_3_img", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_block_4_img != null && page_block_4_img.getSize() > 0 && !contentTypes.contains(page_block_4_img.getContentType())) {
            e.rejectValue("block_4_img", ".", "Неправильное расширение файла!");
        }
        if(page_block_4_img != null && page_block_4_img.getSize() > 19_999_999) {
            e.rejectValue("block_4_img", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_block_5_img != null && page_block_5_img.getSize() > 0 && !contentTypes.contains(page_block_5_img.getContentType())) {
            e.rejectValue("block_5_img", ".", "Неправильное расширение файла!");
        }
        if(page_block_5_img != null && page_block_5_img.getSize() > 19_999_999) {
            e.rejectValue("block_5_img", ".", "Максимальный размер файла - 20 МБ!");
        }
        if(page_block_6_img != null && page_block_6_img.getSize() > 0 && !contentTypes.contains(page_block_6_img.getContentType())) {
            e.rejectValue("block_6_img", ".", "Неправильное расширение файла!");
        }
        if(page_block_6_img != null && page_block_6_img.getSize() > 19_999_999) {
            e.rejectValue("block_6_img", ".", "Максимальный размер файла - 20 МБ!");
        }



        validate(page, e);
    }
}

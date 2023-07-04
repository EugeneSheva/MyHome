package com.example.myhome.validator;

import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.MainPage;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Log
public class AboutPageValidator implements Validator {

    public static List<String> contentTypes = List.of("image/png", "image/jpeg", "image/jpg");

    @Override
    public boolean supports(Class<?> clazz) {
        return AboutPage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AboutPage page = (AboutPage) target;
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

    public void validateAboutPage(Object target, Errors e,MultipartFile page_director_photo,
                                  MultipartFile[] page_photos,
                                  MultipartFile[] page_add_photos,
                                  String[] document_names,
                                  MultipartFile[] document_files) {
        AboutPage page = (AboutPage) target;

        if(page_director_photo != null && page_director_photo.getSize() > 0 && !contentTypes.contains(page_director_photo.getContentType())) {
            e.rejectValue("director_photo", ".", "Неправильное расширение файла!");
        }

        for(MultipartFile photo : page_photos) {
            if(photo != null && photo.getSize() > 0 && !contentTypes.contains(photo.getContentType())) {
                e.rejectValue("page_photos", ".", "Неправильное расширение файлов!");
                break;
            }
        }

        for(MultipartFile photo : page_add_photos) {
            if(photo != null && photo.getSize() > 0 && !contentTypes.contains(photo.getContentType())) {
                e.rejectValue("page_add_photos", ".", "Неправильное расширение файлов!");
                break;
            }
        }

        validate(page, e);
    }
}

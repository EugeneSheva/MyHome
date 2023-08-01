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

    public static List<String> contentTypes = List.of("image/png", "image/jpeg", "image/jpg", "application/pdf");

    @Override
    public boolean supports(Class<?> clazz) {
        return AboutPage.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AboutPage page = (AboutPage) target;

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

    public void validateAboutPage(Object target, Errors e,MultipartFile page_director_photo,
                                  MultipartFile[] page_photos,
                                  MultipartFile[] page_add_photos) {
        AboutPage page = (AboutPage) target;

        if(page_director_photo != null && page_director_photo.getSize() > 0 && !contentTypes.contains(page_director_photo.getContentType())) {
            e.rejectValue("director_photo", ".", "Неправильное расширение файла!");
        }

        for(MultipartFile photo : page_photos) {
            if(photo != null && photo.getSize() > 0 && !contentTypes.contains(photo.getContentType())) {
                e.rejectValue("photos", ".", "Неправильное расширение файлов!");
                break;
            }
            if(photo != null && photo.getSize() > 19_999_999) {
                e.rejectValue("photos", ".", "Максимальный размер файлов - 20 МБ!");
                break;
            }
        }

        for(MultipartFile photo : page_add_photos) {
            if(photo != null && photo.getSize() > 0 && !contentTypes.contains(photo.getContentType())) {
                e.rejectValue("add_photos", ".", "Неправильное расширение файлов!");
                break;
            }
            if(photo != null && photo.getSize() > 19_999_999) {
                e.rejectValue("add_photos", ".", "Максимальный размер файлов - 20 МБ!");
                break;
            }
        }

        if(page.getDocuments() != null) {
            for (int i = 0; i < page.getDocuments().size(); i++) {
                AboutPage.Document currentDocument = page.getDocuments().get(i);
                if(currentDocument.getDocumentName().length() < 2 || currentDocument.getDocumentName().length() > 200) {
                    e.rejectValue("documents["+i+"]", "document.length", "Length of document name: 2-200");
                } else if(currentDocument.getDocumentName().isEmpty()) {
                    e.rejectValue("documents["+i+"]", "document.length", "Document name can't be empty");
                }

                if(currentDocument.getFile() != null && currentDocument.getFile().getSize() > 0) {
                    if(!contentTypes.contains(currentDocument.getFile().getContentType())) {
                        e.rejectValue("documents["+i+"]", "document.file", "Wrong file format (.jpg, .jpeg, .png, .pdf)");
                    } else if (currentDocument.getFile().getSize() > 19_999_999) {
                        e.rejectValue("documents["+i+"]", "document.file", "Max file size = 20 MB");
                    }
                }

            }
        }

        validate(page, e);
    }
}

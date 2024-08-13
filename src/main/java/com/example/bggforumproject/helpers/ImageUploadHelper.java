package com.example.bggforumproject.helpers;

import com.example.bggforumproject.exceptions.IllegalFileUploadException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUploadHelper {
    public static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String FILE_NAME_FORMAT = "%s_%s";

    public static boolean isAllowedExtension(String fileName, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String pattern) {
        long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new IllegalFileUploadException("Max file size is 2MB");
        }

        String fileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        if (!isAllowedExtension(fileName, pattern)) {
            throw new IllegalFileUploadException("Only jpg, png, gif, bmp files are allowed");
        }
    }

    public static String getFileName(final String name) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FILE_NAME_FORMAT, name, date);
    }
}

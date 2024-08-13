package com.example.bggforumproject.service;

import com.cloudinary.Cloudinary;
import com.example.bggforumproject.exceptions.IllegalFileUploadException;
import com.example.bggforumproject.service.contacts.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile multipartFile) throws IOException {
        try {
            Map result = this.cloudinary
                    .uploader()
                    .upload(multipartFile.getBytes(), Collections.emptyMap());
            return result.get("secure_url").toString();
        } catch (Exception e) {
            throw new IllegalFileUploadException("Failed to upload file");
        }
    }
}

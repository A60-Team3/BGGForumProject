package com.example.bggforumproject.service;

import com.cloudinary.Cloudinary;
import com.example.bggforumproject.exceptions.IllegalFileUploadException;
import com.example.bggforumproject.service.contacts.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile multipartFile) throws IOException {
        try {
            return this.cloudinary
                    .uploader()
                    .upload(multipartFile.getBytes(), Collections.emptyMap())
                    .get("secure_url")
                    .toString();
        } catch (Exception e) {
            throw new IllegalFileUploadException("Failed to upload file");
        }
    }
}

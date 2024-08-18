package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.User;

import java.util.List;

public interface PictureService {
    List<ProfilePicture> getAll();

    ProfilePicture get(long userId);

    void savePhoto(String photoUrl, User user);

    void removePhoto(User user);
}

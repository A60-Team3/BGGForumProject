package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.ProfilePicture;

import java.util.List;

public interface PictureRepository {

    List<ProfilePicture> getAll();

    ProfilePicture get(long userId);

    void savePhoto(ProfilePicture profilePicture);

    void updatePhoto(ProfilePicture profilePicture);

    void deletePhoto(ProfilePicture profilePicture);
}

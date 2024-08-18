package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PictureRepository;
import com.example.bggforumproject.service.contacts.PictureService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;

    public PictureServiceImpl(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Override
    public List<ProfilePicture> getAll(){
        return pictureRepository.getAll();
    }

    @Override
    public ProfilePicture get(long userId){
        return pictureRepository.get(userId);
    }

    @Override
    public void savePhoto(String photoUrl, User user) {
        ProfilePicture profilePicture = pictureRepository.get(user.getId());

        if (profilePicture == null) {

            profilePicture = new ProfilePicture(photoUrl);
            profilePicture.setUser(user);

            pictureRepository.savePhoto(profilePicture);
        } else {
            //TODO when updating must delete existing in cloudinary ??
            profilePicture.setPhotoUrl(photoUrl);
            pictureRepository.updatePhoto(profilePicture);
        }
    }

    @Override
    public void removePhoto(User user) {
        ProfilePicture profilePicture = pictureRepository.get(user.getId());

        if (profilePicture != null) {
            pictureRepository.deletePhoto(profilePicture);
        } else {
            throw new EntityNotFoundException(
                    "Picture",
                    "user",
                    user.getFirstName() + " " + user.getLastName());
        }
    }
}

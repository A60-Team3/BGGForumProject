package com.example.bggforumproject.models;

import com.example.bggforumproject.models.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "profile_pictures")
public class ProfilePicture extends BaseEntity {
    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @OneToOne(mappedBy = "profilePicture")
    private User userId;

    public ProfilePicture() {
    }

    public ProfilePicture(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User user) {
        this.userId = user;
    }
}

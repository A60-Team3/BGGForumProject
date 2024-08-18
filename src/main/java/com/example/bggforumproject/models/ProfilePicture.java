package com.example.bggforumproject.models;

import com.example.bggforumproject.models.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "profile_pictures")
public class ProfilePicture extends BaseEntity {
    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

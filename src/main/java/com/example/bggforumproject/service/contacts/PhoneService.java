package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.PhoneNumber;
import com.example.bggforumproject.models.User;

import java.util.List;

public interface PhoneService {
    List<PhoneNumber> getAll();

    String get(long userId);

    void savePhone(String phoneNumber, User user);

    void deletePhone(User user);
}

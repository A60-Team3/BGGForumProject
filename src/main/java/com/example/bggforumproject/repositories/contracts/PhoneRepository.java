package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.PhoneNumber;

import java.util.List;
public interface PhoneRepository {
    List<PhoneNumber> getAll();

    PhoneNumber get(long userId);

    void savePhone(PhoneNumber phoneNumber);

    void updatePhone(PhoneNumber phoneNumber);

    void deletePhone(PhoneNumber phoneNumber);
}

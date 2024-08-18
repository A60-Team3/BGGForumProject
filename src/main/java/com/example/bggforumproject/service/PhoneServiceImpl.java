package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.PhoneNumber;
import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PhoneRepository;
import com.example.bggforumproject.service.contacts.PhoneService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PhoneServiceImpl implements PhoneService {
    private final PhoneRepository phoneRepository;

    public PhoneServiceImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public List<PhoneNumber> getAll() {
        return phoneRepository.getAll();
    }

    @Override
    public String get(long userId) {
        PhoneNumber phoneNumber = phoneRepository.get(userId);
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.getNumber();
    }

    @Override
    public void savePhone(String phoneNumber, User user) {
        PhoneNumber userPhone = phoneRepository.get(user.getId());

        if (userPhone == null) {

            userPhone = new PhoneNumber();
            userPhone.setNumber(phoneNumber);
            userPhone.setUser(user);

            phoneRepository.savePhone(userPhone);
        } else {

            userPhone.setNumber(phoneNumber);
            phoneRepository.updatePhone(userPhone);
        }
    }

    @Override
    public void deletePhone(User user) {
        PhoneNumber phoneNumber = phoneRepository.get(user.getId());

        if (phoneNumber != null) {
            phoneRepository.deletePhone(phoneNumber);
        } else {
            throw new EntityNotFoundException(
                    "Phone number",
                    "user",
                    user.getFirstName() + " " + user.getLastName());
        }
    }
}

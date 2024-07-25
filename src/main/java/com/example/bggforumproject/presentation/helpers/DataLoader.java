package com.example.bggforumproject.presentation.helpers;


import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import com.example.bggforumproject.persistance.repositories.RoleRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.dtos.RegistrationDTO;
import com.example.bggforumproject.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataLoader implements CommandLineRunner {

    private final AuthenticationService authenticationService;
    private final DataSource dataSource;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public DataLoader(AuthenticationService authenticationService, DataSource dataSource, RoleRepository roleRepository, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.dataSource = dataSource;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (roleRepository.findAll().isEmpty()) {
            for (RoleType roleEnum : RoleType.values()) {
                Role userRole = new Role();
                userRole.setAuthority(roleEnum.name());
                roleRepository.create(userRole);
            }
        }

        if (userRepository.findAll().isEmpty()) {
            authenticationService.registerUser(new RegistrationDTO("John", "Doeson", "john.doe@example.com", "john_doe", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Jane", "Smith", "jane.smith@example.com", "jane_smith", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Alice", "Johnson", "jalice.johnson@example.com", "alice_johnson", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Boby", "Brown", "bob.brown@example.com", "bob_brown", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Charlie", "Davis", "charlie.davis@example.com", "charlie_davis", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Evan", "Miller", "eva.miller@example.com", "eva_miller", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Frank", "Wilson", "frank.wilson@example.com", "frank_wilson", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Grace", "Moore", "grace.moore@example.com", "grace_moore", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Hank", "Taylor", "hank.taylor@example.com", "hank_taylor", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Ivylin", "Anderson", "ivy.anderson@example.com", "ivy_anderson", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Frank", "Wilson", "frank.wilson@example.com", "frank_wilson", "password123"));
            authenticationService.registerUser(new RegistrationDTO("Frank", "Wilson", "frank.wilson@example.com", "frank_wilson", "password123"));

            Resource resource = new ClassPathResource("data.sql");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
            populator.execute(dataSource);
        }
    }
}

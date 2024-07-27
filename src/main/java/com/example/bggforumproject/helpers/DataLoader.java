package com.example.bggforumproject.helpers;


import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.RoleRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.service.contacts.AuthenticationService;
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

        if (userRepository.getAll().isEmpty()) {
            authenticationService.registerUser(new User("John", "Doeson", "john.doe@example.com", "john_doe", "password123"));
            authenticationService.registerUser(new User("Jane", "Smith", "jane.smith@example.com", "jane_smith", "password123"));
            authenticationService.registerUser(new User("Alice", "Johnson", "jalice.johnson@example.com", "alice_johnson", "password123"));
            authenticationService.registerUser(new User("Boby", "Brown", "bob.brown@example.com", "bob_brown", "password123"));
            authenticationService.registerUser(new User("Charlie", "Davis", "charlie.davis@example.com", "charlie_davis", "password123"));
            authenticationService.registerUser(new User("Evan", "Miller", "eva.miller@example.com", "eva_miller", "password123"));
            authenticationService.registerUser(new User("Frank", "Wilson", "frank.wilson@example.com", "frank_wilson", "password123"));
            authenticationService.registerUser(new User("Grace", "Moore", "grace.moore@example.com", "grace_moore", "password123"));
            authenticationService.registerUser(new User("Hank", "Taylor", "hank.taylor@example.com", "hank_taylor", "password123"));
            authenticationService.registerUser(new User("Ivylin", "Anderson", "ivy.anderson@example.com", "ivy_anderson", "password123"));


            Resource resource = new ClassPathResource("data.sql");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
            populator.execute(dataSource);
        }
    }
}

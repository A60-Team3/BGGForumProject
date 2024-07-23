package com.example.bggforumproject.security.caseOne;




import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.presentation.helpers.UserMapper;
import com.example.bggforumproject.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public class AuthenticationControllerOne {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthenticationControllerOne(UserMapper mapper, AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDTO registerUserDto) {
        User registeredUser = authenticationService.registerUser(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginUserDTO loginUserDto) {
        User authenticatedUser = authenticationService.loginUser(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}

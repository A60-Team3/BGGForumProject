package com.example.bggforumproject.controllers.rest;


import com.example.bggforumproject.dtos.request.LoginDTO;
import com.example.bggforumproject.dtos.request.RegistrationDTO;
import com.example.bggforumproject.dtos.request.UnknownOutDTO;
import com.example.bggforumproject.dtos.response.ApiErrorResponseDTO;
import com.example.bggforumproject.dtos.response.ResponseDTO;
import com.example.bggforumproject.dtos.response.UserOutDTO;
import com.example.bggforumproject.exceptions.CustomAuthenticationException;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.service.contacts.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@RestController
//@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;


    public AuthenticationController(AuthenticationService authenticationService, AuthenticationManager authenticationManager, ModelMapper mapper) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
    }

    @Operation(summary = "Register account.",
            description = "Allows the user to register an account. Creates new user",
            tags = {"free"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details for registering",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegistrationDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Successfully signed up an account.",
                            content = @Content(schema = @Schema(implementation = UserOutDTO.class))),
                    @ApiResponse(responseCode = "400",
                            description = "The request body is not correct.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "409",
                            description = "The username already exists.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PostMapping("/register")
    public ResponseEntity<UserOutDTO> registerUser(@Valid @RequestBody RegistrationDTO registrationDTO) {

        User user = mapper.map(registrationDTO, User.class);
        User registered = authenticationService.registerUser(user);
        return new ResponseEntity<>(mapper.map(registered, UserOutDTO.class), HttpStatus.CREATED);
    }

    @Operation(summary = "Login into the application.",
            description = "Allows the user to login to existing account.",
            tags = {"free"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Username and password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully logged in with the account.",
                            content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "400",
                            description = "The request body is not correct.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "401",
                            description = "Incorrect username/password or Account doesn't exist.",
                            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
            })
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@Valid @RequestBody LoginDTO body) {
        //TODO login must throw when user already logged in
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.username(), body.password())
            );

            ResponseDTO dto = authenticationService.loginUser(auth);
            return ResponseEntity.ok(dto);

        } catch (AuthenticationException e) {
            throw new CustomAuthenticationException("Invalid username or password");
        }
    }

    @Operation(summary = "Logout procedure.",
            description = "Allows the user to logout. Redirects to main page",
            tags = {"users"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully logged out.",
                            content = @Content(schema = @Schema(implementation = UnknownOutDTO.class))),

            })
    @PostMapping("/logout")
    public void logoutUser() {
    }

}

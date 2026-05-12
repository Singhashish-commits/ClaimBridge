package com.ashish.claimbridgeauthservice.Controller;

import com.ashish.claimbridgeauthservice.Dto.LoginRequest;
import com.ashish.claimbridgeauthservice.Dto.SignUpRequest;
import com.ashish.claimbridgeauthservice.Repository.UserRepository;
import com.ashish.claimbridgeauthservice.Service.AuthService;
import com.ashish.claimbridgeauthservice.Service.JwtService;
import com.ashish.claimbridgeauthservice.Dto.ApiResponse;
import com.ashish.claimbridgeauthservice.Dto.JwtResponse;
import com.ashish.claimbridgeauthservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserRepository userRepo, JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtService = jwtService;

    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);


    }

}

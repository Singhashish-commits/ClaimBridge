package com.ashish.claimbridgeauthservice.Controller;

import com.ashish.claimbridgeauthservice.Dto.*;
import com.ashish.claimbridgeauthservice.Repository.UserRepository;
import com.ashish.claimbridgeauthservice.Service.AuthService;
import com.ashish.claimbridgeauthservice.Service.JwtService;
import com.ashish.claimbridgeauthservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("signUpRequest:  Receiverd here in Controller layer ");
        return authService.registerUser(signUpRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);


    }
    @PostMapping("/create-staff")
    public ResponseEntity<ApiResponse> createStaff(@RequestBody SignUpRequest signUpRequest,
                                                   @RequestHeader("tenantId") String tenantId,
                                                   @RequestHeader("role") String role){

        authService.createStaff(signUpRequest,tenantId,role);
        return new ResponseEntity<>(new ApiResponse("User Created  for the Organization",true), HttpStatus.OK);

    }
//    @PostMapping("/update-profile")
//    public ResponseEntity<ApiResponse> updateDetails(
//            @RequestBody UpdateProfileRequest request,
//            @RequestHeader("tenantId")String tenantId,
//            @RequestHeader("role")String role,
//            ) {
//
//    }

}

package com.ashish.claimbridgeauthservice.Controller;

import com.ashish.claimbridgeauthservice.Dto.*;
import com.ashish.claimbridgeauthservice.Service.AuthService;
import com.ashish.claimbridgeauthservice.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    @Autowired
    public AuthController(AuthService authService,  JwtService jwtService) {
        this.authService = authService;
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
    @PostMapping("/create-staff")
    public ResponseEntity<ApiResponse> createStaff(@RequestBody SignUpRequest signUpRequest,
                                                   @RequestHeader("tenantId") String tenantId,
                                                   @RequestHeader("role") String role){

       return authService.createStaff(signUpRequest,tenantId,role);

    }
    @PostMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateDetails(
            @RequestBody UpdateProfileRequest request,
            @RequestHeader("tenantId")String tenantId,
            @RequestHeader("role")String role) {
       return  authService.updateDetails(request,tenantId,role);
    }

}

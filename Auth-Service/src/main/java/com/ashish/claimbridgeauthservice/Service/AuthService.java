package com.ashish.claimbridgeauthservice.Service;

import com.ashish.claimbridgeauthservice.Dto.LoginRequest;
import com.ashish.claimbridgeauthservice.Dto.SignUpRequest;
import com.ashish.claimbridgeauthservice.Repository.OrganizationRepo;
import com.ashish.claimbridgeauthservice.Repository.UserRepository;
import com.ashish.claimbridgeauthservice.Dto.ApiResponse;
import com.ashish.claimbridgeauthservice.Dto.JwtResponse;
import com.ashish.claimbridgeauthservice.model.Organization;
import com.ashish.claimbridgeauthservice.model.Role;
import com.ashish.claimbridgeauthservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrganizationRepo organizationRepo;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepo, JwtService jwtService, AuthenticationManager authenticationManager, OrganizationRepo organizationRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.organizationRepo = organizationRepo;

    }

    public ResponseEntity<ApiResponse> registerUser(SignUpRequest signUpRequest) {
        if(userRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UsernameNotFoundException("Email already in use");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        Role userRole = Role.valueOf("ROLE_"+signUpRequest.getRole().toUpperCase());
        user.setRole(userRole);
        Organization organization = organizationRepo.findByNameIgnoreCase((signUpRequest.getOrganizationName())).orElseGet(()->{
            Organization newOrg = new Organization();
            newOrg.setName(signUpRequest.getOrganizationName());
            newOrg.setOrgType(Role.valueOf("ROLE_" + signUpRequest.getRole().toUpperCase()));
            String prefix = newOrg.getOrgType()==Role.ROLE_HOSPITAL ? "HOSP_" :"INS_";
            newOrg.setTenantId(prefix + UUID.randomUUID().toString().substring(0,8).toUpperCase());
            return organizationRepo.save(newOrg);
        });
        user.setTenantId(organization.getTenantId());
        user.setOrganizationName(organization.getName());
        userRepo.save(user);
        return ResponseEntity.ok(new ApiResponse("Registered successfully to " + organization.getName(), true));

        }
    public JwtResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        List<String> roles = List.of(user.getRole().name());

        return new JwtResponse(token,  user.getId(), user.getEmail(), roles);

    }


}

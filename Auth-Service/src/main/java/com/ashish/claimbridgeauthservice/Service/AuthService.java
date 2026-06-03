package com.ashish.claimbridgeauthservice.Service;

import com.ashish.claimbridgeauthservice.Dto.*;
import com.ashish.claimbridgeauthservice.Repository.OrganizationRepo;
import com.ashish.claimbridgeauthservice.Repository.UserRepository;
import com.ashish.claimbridgeauthservice.event.HospitalCreateEvent;
import com.ashish.claimbridgeauthservice.event.InsurerCreateEvent;
import com.ashish.claimbridgeauthservice.event.OrganizationUpdateEvent;
import com.ashish.claimbridgeauthservice.model.Organization;
import com.ashish.claimbridgeauthservice.model.Role;
import com.ashish.claimbridgeauthservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final KafkaProducerService kafkaProducerService;
    private final OrganizationRepo organizationRepo;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepo, JwtService jwtService, AuthenticationManager authenticationManager, KafkaProducerService kafkaProducerService, OrganizationRepo organizationRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.kafkaProducerService = kafkaProducerService;
        this.organizationRepo = organizationRepo;
    }

    public ResponseEntity<ApiResponse> registerUser(SignUpRequest signUpRequest) {

        String roleStr = "ROLE_"+signUpRequest.getRole().toUpperCase();
        if (!roleStr.equals("ROLE_HOSPITAL") && !roleStr.equals("ROLE_INSURER")) {
            throw new RuntimeException("Invalid role for Organization registration!");
        }
        if(userRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

                    String prefix = roleStr.equals("ROLE_HOSPITAL") ? "HOSP_" : "INS_";
                    String tenantId =prefix + UUID.randomUUID().toString()
                            .substring(0, 8).toUpperCase();

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.valueOf(roleStr));
        user.setTenantId(tenantId);
        user.setName(signUpRequest.getName());
        user.setOrganizationName(signUpRequest.getOrganizationName());
        Organization org = new Organization();
        org.setTenantId(tenantId);
        org.setName(signUpRequest.getOrganizationName());
        org.setOrgType(roleStr);
        organizationRepo.save(org);
        userRepo.save(user);

        if(roleStr.equals("ROLE_INSURER")) {
            InsurerCreateEvent insurer = new InsurerCreateEvent();
            insurer.setAdminEmail(signUpRequest.getEmail());
            insurer.setInsurerName(signUpRequest.getOrganizationName());
            insurer.setTenantId(tenantId);
            insurer.setAdminName(signUpRequest.getName());
            kafkaProducerService.sendInsurerCreateEvent(insurer);
        }
        if(roleStr.equals("ROLE_HOSPITAL")) {
            HospitalCreateEvent hospital = new HospitalCreateEvent();
            hospital.setAdminEmail(signUpRequest.getEmail());
            hospital.setHospitalName(signUpRequest.getOrganizationName());
            hospital.setAdminName(signUpRequest.getName());
            hospital.setTenantId(tenantId);
            kafkaProducerService.sendHospitalCreateEvent(hospital);
        }
        return ResponseEntity.ok(new ApiResponse("Register Successfully",true));

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


    public ResponseEntity<ApiResponse> createStaff(CreateUser request, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") &&!role.equals("ROLE_INSURER")) {
            throw new RuntimeException("Unauthorized !!");
        }
        String staffRole = role.equals("ROLE_HOSPITAL") ? "ROLE_HOSPITAL_USER" : "ROLE_INSURER_USER";
        User staff = new User();
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode("Welocome@123"));
        staff.setTenantId(tenantId);
        staff.setOrganizationName(request.getOrganizationName());
        staff.setName(request.getName());
        staff.setRole(Role.valueOf(staffRole));
        userRepo.save(staff);
        return new ResponseEntity<>
                (new ApiResponse("User Created for the Organization",true), HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse> updateDetails(UpdateProfileRequest request, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_INSURER")) {
            throw new RuntimeException("Unauthorized  to Update Details of the Organization !!");
        }

        OrganizationUpdateEvent event = new OrganizationUpdateEvent();
        event.setAddress(request.getAddress());
        event.setCity(request.getCity());
        event.setState(request.getState());
        event.setZipCode(request.getZipCode());
        event.setPhone(request.getPhone());
        event.setType(role.equals("ROLE_HOSPITAL")?"HOSPITAL":"INSURER");
        event.setTenantId(tenantId);
        kafkaProducerService.sendOrganizationUpdatevent(event);
        return new ResponseEntity<>
                (new ApiResponse("Organization Updated",true), HttpStatus.OK);
    }
}

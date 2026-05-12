package com.ashish.claimbridgeauthservice.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public - No token needed!";
    }

    @GetMapping("/protected")
    public String protectedEndpoint(
            @RequestHeader(value = "email",required = false) String email,
             @RequestHeader(value="role",required = false) String role,
            @RequestHeader(value="tenantId",required = false)String tenantId,
            @RequestHeader(value="userId",required = false) Long userId
            ){
        return "Protected! User is: " + email+" "+role+" "+tenantId+" "+userId;
    }
}

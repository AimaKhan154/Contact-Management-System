package org.contactmgmt.controller;

import jakarta.validation.Valid;
import org.contactmgmt.dto.ChangePasswordRequest;
import org.contactmgmt.dto.LoginRequest;
import org.contactmgmt.dto.MessageResponse;
import org.contactmgmt.dto.SignupRequest;
import org.contactmgmt.security.UserDetailsImpl;
import org.contactmgmt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.changePassword(request, userDetails);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }
}

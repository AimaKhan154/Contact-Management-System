package org.contactmgmt.service;

import org.contactmgmt.dto.ChangePasswordRequest;
import org.contactmgmt.dto.LoginRequest;
import org.contactmgmt.dto.SignupRequest;
import org.contactmgmt.dto.JwtResponse;
import org.contactmgmt.entity.AppUser;
import org.contactmgmt.repository.AppUserRepository;
import org.contactmgmt.security.JwtUtils;
import org.contactmgmt.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public void registerUser(SignupRequest signUpRequest) {
        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        if (signUpRequest.getPhone() != null && userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new RuntimeException("Error: Phone number is already in use!");
        }

        if (signUpRequest.getEmail() == null && signUpRequest.getPhone() == null) {
            throw new RuntimeException("Error: Either email or phone must be provided.");
        }

        AppUser user = new AppUser(signUpRequest.getEmail(), signUpRequest.getPhone(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
        logger.info("User registered successfully. Email: {}, Phone: {}", signUpRequest.getEmail(), signUpRequest.getPhone());
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User authenticated successfully. Username: {}", loginRequest.getUsername());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getPhone());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, UserDetailsImpl userDetails) {
        AppUser user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Error: Incorrect old password.");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("User {} changed their password.", userDetails.getUsername());
    }
}

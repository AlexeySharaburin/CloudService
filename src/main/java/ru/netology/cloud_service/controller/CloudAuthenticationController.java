package ru.netology.cloud_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloud_service.model.AuthRequest;
import ru.netology.cloud_service.model.AuthToken;
import ru.netology.cloud_service.service.CloudAuthenticationService;

import java.security.SecureRandom;

@RestController
@RequestMapping("/")
public class CloudAuthenticationController {

    private final CloudAuthenticationService cloudAuthenticationService;

    public CloudAuthenticationController(CloudAuthenticationService cloudAuthenticationService) {
        this.cloudAuthenticationService = cloudAuthenticationService;
    }

    @GetMapping("/encode")
    public String encodePassword(String password) {
        System.out.println("Ваш пароль: " + password);
        int strength = 10; // work factor of bcrypt
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder(strength, new SecureRandom());
        String encodePassword = bCryptPasswordEncoder.encode(password);
        System.out.println("Ваш закодированный пароль: " + encodePassword);
        return encodePassword;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody AuthRequest authRequest) throws Exception {
        final String token = cloudAuthenticationService.createAuthenticationToken(authRequest);
        return token != null
                ? new ResponseEntity<>(new AuthToken(token), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
        final Boolean isRemove = cloudAuthenticationService.removeToken(authToken);
        return isRemove
                ? new ResponseEntity<>("Success logout", HttpStatus.OK)
                : new ResponseEntity<>("Unsuccess logout", HttpStatus.NOT_FOUND);
    }

}
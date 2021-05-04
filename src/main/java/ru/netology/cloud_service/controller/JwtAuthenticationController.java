package ru.netology.cloud_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloud_service.component.JwtTokenUtil;
import ru.netology.cloud_service.model.AuthToken;
import ru.netology.cloud_service.model.MyUserPrincipal;
import ru.netology.cloud_service.model.UserEntity;
import ru.netology.cloud_service.service.JwtUserDetailsService;

import java.security.Principal;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthToken> createAuthenticationToken (@RequestBody UserEntity userEntity) throws Exception {

        System.out.println("Пришёл клиент с login/password - " + userEntity.getUsername() + "/"  + userEntity.getPassword());

        var user = jwtUserDetailsService.loadUserByUsername(userEntity.getUsername());

        System.out.println("Клиент из базы данных с login/password - " + user.getUsername() + "/"  + user.getPassword());

        authenticate(userEntity.getUsername(), userEntity.getPassword());

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEntity.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthToken(token));
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS!", e);
        }
    }
}

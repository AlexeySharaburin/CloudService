package ru.netology.cloud_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.cloud_service.component.JwtTokenUtil;
import ru.netology.cloud_service.exception.ErrorBadCredentials;
import ru.netology.cloud_service.exception.ErrorUnauthorized;
import ru.netology.cloud_service.model.AuthRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CloudAuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    public CloudAuthenticationService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService jwtUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    public final Map<String, String> tokenRepository = new ConcurrentHashMap<>();

    public String createAuthenticationToken(AuthRequest authRequest) throws Exception {
        String username = authRequest.getLogin();
        authenticate(username, authRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        tokenRepository.put(token, username);
        return token;
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new ErrorUnauthorized("Unauthorized error");
        } catch (BadCredentialsException e) {
            throw new ErrorBadCredentials("Bad Credentials");
        }
    }

    public Boolean removeToken(String authToken) {
        String token = authToken.substring(7);
        return tokenRepository.remove(token) != null;
    }

}

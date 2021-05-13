package ru.netology.cloud_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.cloud_service.component.JwtTokenUtil;
import ru.netology.cloud_service.model.AuthRequest;
import ru.netology.cloud_service.model.AuthToken;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.repository.CloudServiceRepository;
import ru.netology.cloud_service.repository.StorageRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CloudServiceService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;



    private final CloudServiceRepository cloudServiceRepository;

    public CloudServiceService(CloudServiceRepository cloudServiceRepository) {
        this.cloudServiceRepository = cloudServiceRepository;
    }

    public Map<String, String> tokenRepository = new ConcurrentHashMap<>();


    public String createAuthenticationToken(AuthRequest authRequest) throws Exception {
        String username = authRequest.getLogin();
        authenticate(username, authRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        tokenRepository.put(token, username);
        return token;
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

    public Boolean removeToken(String authToken) {
        if (tokenRepository.remove(authToken) != null) {
            return true;
        }
        return false;
    }


    public List<FileRequest> listFiles(String authToken) {
        System.out.println("Service");
        String username = tokenRepository.get(authToken);
        List<FileRequest> files = cloudServiceRepository.listFiles(username);
        if (!files.isEmpty()) {
            return files;
        }

        return null;
    }




}

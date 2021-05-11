package ru.netology.cloud_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloud_service.model.AuthRequest;
import ru.netology.cloud_service.model.AuthToken;
import ru.netology.cloud_service.service.CloudServiceService;

@RestController
public class CloudServiceController {

    private final CloudServiceService cloudServiceService;

    public CloudServiceController(CloudServiceService cloudServiceService) {
        this.cloudServiceService = cloudServiceService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody AuthRequest authRequest) throws Exception {
        final String token = cloudServiceService.createAuthenticationToken(authRequest);
        return new ResponseEntity<>(new AuthToken(token), HttpStatus.OK);
    }

}


//@RestController
//public class CloudServiceController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;
//
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthToken> createAuthenticationToken (@RequestBody AuthRequest authRequest) throws Exception {
//
//        System.out.println("Пришёл клиент с login/password - " + authRequest.getLogin() + "/"  + authRequest.getPassword());
//
//        var user = jwtUserDetailsService.loadUserByUsername(authRequest.getLogin());
//
//        System.out.println("Клиент из базы данных с login/password - " + user.getUsername() + "/"  + user.getPassword());
//
//        authenticate(authRequest.getLogin(), authRequest.getPassword());
//
//        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authRequest.getLogin());
//
//        System.out.println("User Details: " + userDetails);
//
//        final String token = jwtTokenUtil.generateToken(userDetails);
//
//        System.out.println("Token: " + token);
//
//        return new ResponseEntity<>(new AuthToken(token), HttpStatus.OK);
//    }
//
//
//    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS!", e);
//        }
//    }
//}
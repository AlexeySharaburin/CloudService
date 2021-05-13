package ru.netology.cloud_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloud_service.model.AuthRequest;
import ru.netology.cloud_service.model.AuthToken;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.service.CloudServiceService;

import java.util.List;

@RestController
//@RequestMapping("/")
public class CloudServiceController {

    private final CloudServiceService cloudServiceService;

    public CloudServiceController(CloudServiceService cloudServiceService) {
        this.cloudServiceService = cloudServiceService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody AuthRequest authRequest) throws Exception {
        System.out.println("Controller_login");
        final String token = cloudServiceService.createAuthenticationToken(authRequest);
        return token != null
                ? new ResponseEntity<>(new AuthToken(token), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



//    @RequestMapping(value = "/logout",
//            produces = "application/json",
//            method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) throws Exception {
        System.out.println("Controller_logout");
        System.out.println(authToken);
        final Boolean isRemove = cloudServiceService.removeToken(authToken);
        return isRemove
                ? new ResponseEntity<>("Token is removed", HttpStatus.OK)
                : new ResponseEntity<>("Token is not removed", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileRequest>> listFiles(@RequestHeader("auth-token") String authToken) throws Exception {
        final List<FileRequest> files = cloudServiceService.listFiles(authToken);
        return !files.isEmpty()
                ? new ResponseEntity<>(files, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
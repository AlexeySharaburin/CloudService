package ru.netology.cloud_service.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import ru.netology.cloud_service.model.AuthRequest;
import ru.netology.cloud_service.model.AuthToken;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.model.Limit;
import ru.netology.cloud_service.service.CloudServiceService;

import java.security.SecureRandom;
import java.util.List;

@RestController
@RequestMapping("/")
public class CloudServiceController {

    private final CloudServiceService cloudServiceService;

    public CloudServiceController(CloudServiceService cloudServiceService) {
        this.cloudServiceService = cloudServiceService;
    }

//    @Bean(name = "multipartResolver")
//    public CommonsMultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(100000);
//        return multipartResolver;
//    }

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
        System.out.println("Controller_login");
        final String token = cloudServiceService.createAuthenticationToken(authRequest);
        return token != null
                ? new ResponseEntity<>(new AuthToken(token), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) throws Exception {
        System.out.println("Controller_logout. Auth-token: " + authToken);
        final Boolean isRemove = cloudServiceService.removeToken(authToken);
        return isRemove
                ? new ResponseEntity<>("Token is removed", HttpStatus.OK)
                : new ResponseEntity<>("Token is not removed", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileRequest>> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") int limit) throws Exception {
        System.out.println("Controller_filesList. Auth-token: " + authToken);
        System.out.println("Controller_list. Limit: " + limit);
        final List<FileRequest> files = cloudServiceService.getAllFiles(authToken, limit);
        files.forEach(System.out::println);
        return !files.isEmpty()
                ? new ResponseEntity<>(files, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFileToServer(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String fileName, MultipartFile file) throws Exception {
        System.out.println("Controller_uploadFiles. Auth-token: " + authToken);
        System.out.println("Controller_uploadFiles. FileName: " + fileName);
        boolean successUpload = cloudServiceService.uploadFileToServer(authToken, fileName, file);
        return successUpload
                ? new ResponseEntity<>("Success upload " + fileName, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping ("/delete")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String fileName) throws Exception {
        System.out.println("Controller_deleteFiles. Auth-token: " + authToken);
        System.out.println("Controller_deleteFiles. FileName: " + fileName);
        boolean successDelete = cloudServiceService.deleteFile(authToken, fileName);
        return successDelete
                ? new ResponseEntity<>("Success deleted " + fileName, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
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
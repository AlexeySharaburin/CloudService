package ru.netology.cloud_service.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import ru.netology.cloud_service.exception.*;
import ru.netology.cloud_service.model.*;
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
                ? new ResponseEntity<>("Success logout", HttpStatus.OK)
                : new ResponseEntity<>("Unsuccess logout", HttpStatus.NOT_FOUND);
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

    @DeleteMapping ("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) throws Exception {
        System.out.println("Controller_deleteFiles. Auth-token: " + authToken);
        System.out.println("Controller_deleteFiles. FileName: " + filename);
        boolean successDelete = cloudServiceService.deleteFile(authToken, filename);
        return successDelete
                ? new ResponseEntity<>("Success deleted " + filename, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @PutMapping ("/file")
    public ResponseEntity<String> renameFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String currentFilename, @RequestBody NewFilename testFilename) throws Exception {
        System.out.println("Controller_renameFiles. Auth-token: " + authToken);
        System.out.println("Controller_renameFiles. FileName: " + currentFilename);
        System.out.println("Controller_renameFiles. FileName: " + testFilename.getFilename());
        String newFilename = cloudServiceService.renameFile(authToken, currentFilename);
        return (newFilename != null)
                ? new ResponseEntity<>("Success upload from" + currentFilename + "  to " + newFilename, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/file")
    public ResponseEntity<String> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) throws Exception {
        System.out.println("Controller_downloadFiles. Auth-token: " + authToken);
        System.out.println("Controller_downloadFiles. FileName: " + filename);
        boolean successDownload = cloudServiceService.downloadFile(authToken, filename);
        return successDownload
                ? new ResponseEntity<>("Success downloaded " + filename, HttpStatus.OK)
                : new ResponseEntity<>("Error input data", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorInputData.class)
    public ResponseEntity<ExceptionResponse> handleErrorInputData(ErrorInputData e) {
        String msgInput = "Error input data";
        System.out.println(msgInput);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 400), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorBadCredentials.class)
    public ResponseEntity<ExceptionResponse> handleErrorBadCredentials(ErrorBadCredentials e) {
        String msgInput = "Bad Credentials";
        System.out.println(msgInput);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 400), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorUnauthorized.class)
    public ResponseEntity<ExceptionResponse> handleErrorUnauthorized(ErrorUnauthorized e) {
        String msgTransfer = "Unauthorizated error";
        System.out.println(msgTransfer);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 401), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorDeleteFile.class)
    public ResponseEntity<ExceptionResponse> handleErrorDeleteFile(ErrorDeleteFile e) {
        String msgConfirmation = "Error delete file";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorUploadFile.class)
    public ResponseEntity<ExceptionResponse> handleErrorUploadFile(ErrorUploadFile e) {
        String msgConfirmation = "Error upload file";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorDownloadFile.class)
    public ResponseEntity<ExceptionResponse> handleErrorDownloadFile(ErrorDownloadFile e) {
        String msgConfirmation = "Error download file";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorGettingFileList.class)
    public ResponseEntity<ExceptionResponse> handleErrorGettingFileList(ErrorGettingFileList e) {
        String msgConfirmation = "Error getting file list";
        System.out.println(msgConfirmation);
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

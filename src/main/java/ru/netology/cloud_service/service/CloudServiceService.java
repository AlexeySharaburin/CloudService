package ru.netology.cloud_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service.component.JwtTokenUtil;
import ru.netology.cloud_service.model.*;
import ru.netology.cloud_service.repository.CloudServiceRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
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
        System.out.println("Service_login. Username: " + authRequest.getLogin());
        String username = authRequest.getLogin();
        authenticate(username, authRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        System.out.println("Service_login. Token: " + token);
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
        String token = authToken.substring(7);
        System.out.println("Service_logout. Token: " + token);
        System.out.println("Service_logout");
        if (tokenRepository.remove(token) != null) {
            return true;
        }
        return false;
    }


    public List<FileRequest> getAllFiles(String authToken, int limit) {
        String token = authToken.substring(7);
        System.out.println("Service_list. Token: " + token);
        String username = tokenRepository.get(token);
        System.out.println("Service_list. Username: " + username);
        List<FileRequest> files = cloudServiceRepository.getAllFiles(username, limit);
        if (!files.isEmpty()) {
            return files;
        }
        return null;
    }

    public Boolean uploadFileToServer(String authToken, String fileName, MultipartFile file) {
        String token = authToken.substring(7);
        System.out.println("Service_upload. Token: " + token);
        String username = tokenRepository.get(token);
        System.out.println("Service_upload. Username: " + username);
        UserData currentUser = cloudServiceRepository.getUser(username);
        long currentUserId = currentUser.getId();

        if (currentUser != null) {

            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();

                    List<FileRequest> files = cloudServiceRepository.getFileNamesFromStorage(currentUserId);

                    if (existFile(files, fileName)) {
                        fileName = fileName + "(1)";
                    }

                    String dataPath = currentUser.getDataPath();

                    File directoryOfUser = new File(dataPath);

                    if (!directoryOfUser.exists()) {
                        directoryOfUser.mkdirs();
                    }

                    File uploadedFile = new File(directoryOfUser.getAbsolutePath() + File.separator + fileName);

                    try (var out = new FileOutputStream(uploadedFile);
                         var bos = new BufferedOutputStream(out)) {
                        bos.write(bytes, 0, bytes.length);

                        Storage newFile = Storage.builder()
                                .fileName(fileName)
                                .isExist(true)
                                .date(new Date())
                                .userId(currentUserId)
                                .fileSize(file.getSize())
                                .build();

                        if (cloudServiceRepository.saveFile(newFile)) {
                            System.out.println("Service_upload. Success upload " + fileName);
                            return true;
                        }
                        return false;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return false;
    }

    public Boolean deleteFile(String authToken, String fileName) {
        String token = authToken.substring(7);
        System.out.println("Service_delete. Token: " + token);
        String username = tokenRepository.get(token);
        System.out.println("Service_delete. Username: " + username);
        UserData currentUser = cloudServiceRepository.getUser(username);
        long currentUserId = currentUser.getId();

        if (currentUser != null) {

            List<FileRequest> files = cloudServiceRepository.getFileNamesFromStorage(currentUserId);

            if (existFile(files, fileName)) {
                if (cloudServiceRepository.deleteFile(fileName)) {
                    System.out.println("Service_delete. Success deleted " + fileName);
                    return true;
                } else {
                    System.out.println("File not found");
                }
            } else {
                System.out.println("User not found");
            }
        }
        return false;
    }

    public boolean existFile(List<FileRequest> files, String fileName) {
        for (FileRequest fileRequest : files) {
            if (fileRequest.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

//    public List<FileRequest> getFileNamesFromStorage(long userId) {
//        List<Storage> listStorage = storageRepository.findByUserId(userId);
//        List<FileRequest> files = new ArrayList<>();
//        for (Storage storage : listStorage) {
//            if (storage.getIsExist()) {
//                files.add(new FileRequest(storage.getFileName(), storage.getFileSize()));
//            }
//        }
//        return files;
//    }


}

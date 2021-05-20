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
import ru.netology.cloud_service.exception.ErrorBadCredentials;
import ru.netology.cloud_service.exception.ErrorDeleteFile;
import ru.netology.cloud_service.exception.ErrorUnauthorized;
import ru.netology.cloud_service.exception.ErrorUploadFile;
import ru.netology.cloud_service.model.AuthRequest;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.model.Storage;
import ru.netology.cloud_service.model.UserData;
import ru.netology.cloud_service.repository.CloudServiceRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            throw new ErrorUnauthorized("Unauthorized error");
        } catch (BadCredentialsException e) {
            throw new ErrorBadCredentials("Bad Credentials");
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
        String username = tokenRepository.get(token);
        long userId = cloudServiceRepository.getUser(username).getId();
        List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(userId);
        return files.stream()
                .limit(limit)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public Boolean uploadFileToServer(String authToken, String filename, MultipartFile file) {
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

                    List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUserId);

                    if (existFile(files, filename)) {
                        filename = filename + "(1)";
                    }
                    String dataPath = currentUser.getDataPath();
                    File directoryOfUser = new File(dataPath);

                    if (!directoryOfUser.exists()) {
                        directoryOfUser.mkdirs();
                    }
                    File uploadedFile = new File(directoryOfUser.getAbsolutePath() + File.separator + filename);

                    try (var out = new FileOutputStream(uploadedFile);
                         var bos = new BufferedOutputStream(out)) {
                        bos.write(bytes, 0, bytes.length);

                        Storage newFile = Storage.builder()
                                .filename(filename)
                                .isExist(true)
                                .date(new Date())
                                .userId(currentUserId)
                                .fileSize(file.getSize())
                                .build();
                        if (cloudServiceRepository.saveFile(newFile)) {
                            System.out.println("Service_upload. Success upload " + filename);
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File not found");
                throw new ErrorUnauthorized("Error input data");
            }
        } else {
            System.out.println("User not found");
            throw new ErrorUnauthorized("Unauthorized error");
        }

        return false;
    }

    public Boolean deleteFile(String authToken, String filename) {
        String token = authToken.substring(7);
        System.out.println("Service_delete. Token: " + token);
        String username = tokenRepository.get(token);
        System.out.println("Service_delete. Username: " + username);
        UserData currentUser = cloudServiceRepository.getUser(username);
        long currentUserId = currentUser.getId();

        if (currentUser != null) {

            List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUserId);

            if (existFile(files, filename)) {
                if (cloudServiceRepository.deleteFile(filename, currentUserId)) {
                    System.out.println("Service_delete. Success deleted " + filename);
                    return true;
                } else {
                    System.out.println("File not found");
                    throw new ErrorDeleteFile("Error delete file");
                }
            } else {
                System.out.println("User not found");
                throw new ErrorUnauthorized("Unauthorized error");
            }
        }
        return false;
    }


    public String renameFile(String authToken, String currentFilename) {
        Scanner scanner = new Scanner(System.in);
        String token = authToken.substring(7);
//        System.out.println("Service_rename. Token: " + token);
        String username = tokenRepository.get(token);
//        System.out.println("Service_rename. Username: " + username);
        UserData currentUser = cloudServiceRepository.getUser(username);
        long currentUserId = currentUser.getId();

        if (currentUser != null) {

            List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUserId);

            if (existFile(files, currentFilename)) {

                System.out.printf("Rename file %s?(y/n): ", currentFilename);
                String answer = scanner.next();
                if (answer.equals("y")) {
                    String newFilename;
                    while (true) {
                        System.out.print("Input new filename: ");
                        newFilename = scanner.next();
                        if (!existFile(files, newFilename)) {
                            break;
                        } else {
                            System.out.println("This filename already exist!");
                            throw new ErrorUploadFile("Error upload file");
                        }
                    }
                    if (cloudServiceRepository.renameFile(currentFilename, newFilename, currentUserId, username) != null) {
                        System.out.println("Service_rename. Success rename " + newFilename);
                        return newFilename;
                    }
                } else {
                    System.out.println("File name remains unchanged");
                    throw new ErrorUnauthorized("Error input data");
                }
            } else {
                System.out.println("File not found");
                throw new ErrorUploadFile("Error upload file");
            }
        } else {
            System.out.println("User not found");
            throw new ErrorUnauthorized("Unauthorized error");
        }
        return null;
    }


    public Boolean downloadFile(String authToken, String filename) throws MalformedURLException {
        String token = authToken.substring(7);
//        System.out.println("Service_download. Token: " + token);
        String username = tokenRepository.get(token);
//        System.out.println("Service_download. Username: " + username);
        UserData currentUser = cloudServiceRepository.getUser(username);
        long currentUserId = currentUser.getId();

        if (currentUser != null) {

            List<FileRequest> files = cloudServiceRepository.getFilenamesFromStorage(currentUserId);

            if (existFile(files, filename)) {

                String dataPath = currentUser.getDataPath();

                String pathFile = dataPath + File.separator + filename;

                try (var fin = new FileInputStream(pathFile);
                     var bis = new BufferedInputStream(fin, 1024);
                     var out = new FileOutputStream(filename);
                     var bos = new BufferedOutputStream(out, 1024)) {
                    int i = 0;
                    while ((i = bis.read()) != -1) {
                        bos.write(i);
                    }
                    System.out.println("Service_download. Success download " + filename);
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("File not found");
                throw new ErrorUploadFile("Error download file");
            }
        } else {
            System.out.println("User not found");
            throw new ErrorUnauthorized("Unauthorized error");
        }
        return false;
    }

    public boolean existFile(List<FileRequest> files, String filename) {
        for (FileRequest fileRequest : files) {
            if (fileRequest.getFilename().equals(filename)) {
                return true;
            }
        }
        return false;
    }
}


//    public List<FileRequest> getAllFiles(String authToken, int limit) {
//        String token = authToken.substring(7);
//        System.out.println("Service_list. Token: " + token);
//        String username = tokenRepository.get(token);
//        System.out.println("Service_list. Username: " + username);
//        List<FileRequest> files = cloudServiceRepository.getAllFiles(username, limit);
//        if (!files.isEmpty()) {
//            return files;
//        }
//        return null;
//    }
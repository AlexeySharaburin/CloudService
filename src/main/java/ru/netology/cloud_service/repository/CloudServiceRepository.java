package ru.netology.cloud_service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ru.netology.cloud_service.exception.ErrorDeleteFile;
import ru.netology.cloud_service.exception.ErrorUnauthorized;
import ru.netology.cloud_service.exception.ErrorUploadFile;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.model.Storage;
import ru.netology.cloud_service.model.UserData;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class CloudServiceRepository {

    private final UserDataRepository userDataRepository;

    private final StorageRepository storageRepository;

    public CloudServiceRepository(UserDataRepository userDataRepository, StorageRepository storageRepository) {
        this.userDataRepository = userDataRepository;
        this.storageRepository = storageRepository;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }

    @Value("${general.path}")
    private String generalPath;
    @Value("${download.user.folder.path}")
    private String downloadUserPath;
    @Value("${download.external.folder.path}")
    private String downloadExternalPath;

    public UserData getUserByUsername(String username) {
        UserData currentUser = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
        if (currentUser.getIsEnable()) {
            return currentUser;
        } else {
            System.out.println("User not found");
            throw new ErrorUnauthorized("Unauthorized error");
        }
    }

    public List<FileRequest> getFilenamesFromStorage(long userId) {
        List<Storage> listStorage = storageRepository.findByUserIdAndIsExist(userId, true);
        List<FileRequest> files = new ArrayList<>();
        for (Storage storage : listStorage) {
            if (storage.getIsExist()) {
                files.add(new FileRequest(storage.getFilename(), storage.getFileSize()));
            }
        }
        return files;
    }

    public Boolean saveFile(Storage storage) {
        var newStorage = storageRepository.save(storage);
        return newStorage != null;
    }

//    //delete - пометить файл как удалённый
//    public Boolean deleteFile(String filename, long userId) {
//            if (!existFileStorage(userId, filename)) {
//            System.out.println("File not found");
//            throw new ErrorDeleteFile("Error delete file");
//        }
//        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, userId);
//        File currentFile = new File(getAbsolutePathFile(userId, filename));
//        File deletedFile = new File(getAbsolutePathFile(userId, filename) + "_deleted at " + new Date());
//        currentFile.renameTo(deletedFile);
//        currentStorage.setIsExist(false);
//        currentStorage.setFilename(filename + "_deleted at " + new Date());
//        if (!currentStorage.getIsExist()) {
//            System.out.println("Repo_deleted. Deleted");
//            return true;
//        }
//        System.out.println("Repo_deleted. No deleted");
//        return false;
//    }

    // delete - удалить файл
    public Boolean deleteFile(String filename, long userId) {
        if (!existFileStorage(userId, filename)) {
            System.out.println("File not found");
            throw new ErrorDeleteFile("Error delete file");
        }
        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, userId);
        long currentId = currentStorage.getId();
        System.out.println("Repo_deleted. Id " + currentId);
        File currentFile = new File(getAbsolutePathFile(userId, filename));
        storageRepository.deleteById(currentId);
        if (!storageRepository.existsById(currentId) && currentFile.delete()) {
            System.out.println("Repo_deleted. Deleted");
            return true;
        }
        System.out.println("Repo_deleted. No deleted");
        return false;
    }

    public String renameFile(String currentFilename, String newFilename, long userId) {
        Storage currentStorage = storageRepository.findByFilenameAndUserId(currentFilename, userId);
        UserData currentUser = userDataRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (!existFileStorage(userId, currentFilename)) {
            System.out.println("File not found");
            throw new ErrorUploadFile("Error upload file");
        }
        if (currentStorage.getIsExist() && (currentUser != null)) {
            File currentFile = new File(getAbsolutePathFile(userId, currentFilename));
            File newFile = new File(getAbsolutePath(userId) + File.separator + newFilename);
            if (currentFile.renameTo(newFile)) {
                currentStorage.setFilename(newFilename);
                return newFilename;
            }
        }
        return null;
    }

    public boolean uploadFile(MultipartFile file, long userId, String filename) throws IOException {
        String checkedFilename = checkFilenameStorage(userId, filename);
        String dataPath = getAbsolutePath(userId);

        File directoryOfUser = new File(dataPath);
        if (!directoryOfUser.exists()) {
            directoryOfUser.mkdirs();
        }

        String checkNameUploadedFile = checkFilenameFolder(dataPath, filename);
        String pathUploadedFile = getAbsolutePathFile(userId, checkNameUploadedFile);
        File uploadedFile = new File(pathUploadedFile);

        byte[] bytes = file.getBytes();
        try (var out = new FileOutputStream(uploadedFile);
             var bos = new BufferedOutputStream(out)) {
            bos.write(bytes, 0, bytes.length);
            Storage newFile = Storage.builder()
                    .filename(checkedFilename)
                    .isExist(true)
                    .date(new Date())
                    .userId(userId)
                    .fileSize(file.getSize())
                    .build();
            if (saveFile(newFile)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MultipartFile downloadFileFromServer(long userId, String filename) {
        if (!existFileStorage(userId, filename)) {
            System.out.println("File not found");
            throw new ErrorUploadFile("Error download file");
        }

        String downloadPathUser = downloadUserPath + File.separator;
        String downloadExternalUser = downloadExternalPath + File.separator;

        String checkNameDownloadedUserFile = checkFilenameFolder(downloadPathUser, filename);
        String checkNameDownloadedExternalFile = checkFilenameFolder(downloadExternalUser, filename);
        System.out.println(checkNameDownloadedUserFile + " " + checkNameDownloadedExternalFile);
        String pathFile = getAbsolutePathFile(userId, filename);

        try (var fin = new FileInputStream(pathFile);
             var bis = new BufferedInputStream(fin, 1024);
             var outUser = new FileOutputStream(downloadPathUser + checkNameDownloadedUserFile);
             var outExternal = new FileOutputStream(downloadExternalUser + File.separator + checkNameDownloadedExternalFile);
             var bosUser = new BufferedOutputStream(outUser, 1024);
             var bosExternal = new BufferedOutputStream(outExternal, 1024)) {
            int i;
            System.out.println("Loading file...");
            while ((i = bis.read()) != -1) {
                bosUser.write(i);
                bosExternal.write(i);
            }
            System.out.println("Repository_download. Success download file " + filename);
            return convertFiletoMultiPart(pathFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // DownloadMultipartFileFromServerToFront - v.1
    public MultipartFile convertFiletoMultiPart(String pathFile) throws IOException {
        File file = new File(pathFile);
        System.out.println("Path: " + file.toPath());
        FileItem fileItem;
        fileItem = new DiskFileItem("mainFile",
                Files.probeContentType(file.toPath()),
                false,
                file.getName(),
                (int) file.length(),
                file.getParentFile());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileItem.getOutputStream();
        System.out.println("FileItem: " + fileItem.toString());
        return new CommonsMultipartFile(fileItem);
    }

//    // DownloadMultipartFileFromServerToFront - v.2
//    public MultipartFile convertFiletoMultiPart(String pathFile) throws IOException {
//        MultipartFile multipartFile = null;
//        try {
//            File file = new File(pathFile);
//            if (file.exists()) {
//                System.out.println("File Exist => " + file.getName() + " :: " + file.getAbsolutePath());
//            }
//            FileInputStream input = new FileInputStream(file);
//            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
//                    IOUtils.toByteArray(input));
//            System.out.println("multipartFile => " + multipartFile.isEmpty() + " :: "
//                    + multipartFile.getOriginalFilename() + " :: " + multipartFile.getName() + " :: "
//                    + multipartFile.getSize() + " :: " + multipartFile.getBytes());
//        } catch (IOException e) {
//            System.out.println("Exception => " + e.getLocalizedMessage());
//        }
//        return multipartFile;
//    }

    public String checkFilenameStorage(long userId, String filename) {
        String newFilename = filename;
        if (existFileStorage(userId, filename)) {
            System.out.printf("File %s exist\n", filename);
            int i = 0;
            while (true) {
                i++;
                String[] partsName = filename.split("\\.");
                newFilename = partsName[0] + "(" + i + ")." + partsName[1];
                if (!existFileStorage(userId, newFilename)) {
                    System.out.printf("File saved as %s\n", newFilename);
                    break;
                }
            }
        }
        return newFilename;
    }

    public String checkFilenameFolder(String path, String filename) {
        String newFilename = filename;
        if (existFileFolder(path, filename)) {
            int i = 0;
            while (true) {
                i++;
                String[] partsName = filename.split("\\.");
                newFilename = partsName[0] + "(" + i + ")." + partsName[1];
                if (!existFileFolder(path, newFilename)) {
                    System.out.printf("File saved as %s\n", newFilename);
                    break;
                }
            }
        }
        return newFilename;
    }

    public boolean existFileStorage(long userId, String filename) {
        List<FileRequest> files = getFilenamesFromStorage(userId);
        for (FileRequest fileRequest : files) {
            if (fileRequest.getFilename().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public boolean existFileFolder(String path, String filename) {
        File folder = new File(path);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (!file.isDirectory()) {
                    if (filename.equals(file.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getLocalDataPath(long userId) {
        UserData currentUser = userDataRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        return currentUser.getDataPath();
    }

    public String getAbsolutePath(long userId) {
        return generalPath + File.separator + getLocalDataPath(userId);
    }

    public String getAbsolutePathFile(long userId, String filename) {
        return generalPath + File.separator + getLocalDataPath(userId) + File.separator + filename;
    }
}




























//    public File makeUploadedFile(long userId, String filename) {
//        List<FileRequest> files = getFilenamesFromStorage(userId);
//        int i = 0;
//        if (existFile(files, filename)) {
//            while (true) {
//                i++;
//                filename = filename + "_" + i;
//                if (!existFile(files, filename)) {
//                    break;
//                }
//            }
//        }
//        String dataPath = getAbsolutePath(userId);
//        String filePath = getAbsolutePathFile(userId, filename);
//        File directoryOfUser = new File(dataPath);
//        System.out.println("Service_upload. Path: " + filePath);
//        if (!directoryOfUser.exists()) {
//            directoryOfUser.mkdirs();
//        }
//        return new File(filePath);
//    }

//    public boolean downloadFileFromServer(long userId, String filename) {
//        List<FileRequest> files = getFilenamesFromStorage(userId);
//        if (!existFile(files, filename)) {
//            System.out.println("File not found");
//            throw new ErrorUploadFile("Error download file");
//        }
//        String pathFile = getAbsolutePathFile(userId, filename);
//        try (var fin = new FileInputStream(pathFile);
//             var bis = new BufferedInputStream(fin, 1024);
//             var outUser = new FileOutputStream(downloadUserPath + File.separator + filename);
//             var outExternal = new FileOutputStream(downloadExternalPath + File.separator + filename);
//             var bosUser = new BufferedOutputStream(outUser, 1024);
//             var bosExternal = new BufferedOutputStream(outExternal, 1024)) {
//            int i;
//            while ((i = bis.read()) != -1) {
//                bosUser.write(i);
//                bosExternal.write(i);
//            }
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public File makeUploadedFile(long userId, String filename) {
////        List<FileRequest> files = getFilenamesFromStorage(userId);
////        int i = 0;
////        if (existFile(files, filename)) {
////            while (true) {
////                i++;
////                filename = filename + "_" + i;
////                if (!existFile(files, filename)) {
////                    break;
////                }
////            }
////        }
//        String checkedFilename = checkFilename(filename);
//        String dataPath = getAbsolutePath(userId);
//        String pathFile = getAbsolutePathFile(userId, checkedFilename);
//        File directoryOfUser = new File(dataPath);
//        System.out.println("Service_upload. Path: " + pathFile);
//        if (!directoryOfUser.exists()) {
//            directoryOfUser.mkdirs();
//        }
//        return new File(pathFile);
//    }

//@Repository
//@Transactional
//public class CloudServiceRepository {
//
//    //    @Autowired
//    private final UserDataRepository userDataRepository;
//
//    //    @Autowired
//    private final StorageRepository storageRepository;
//
//    public CloudServiceRepository(UserDataRepository userDataRepository, StorageRepository storageRepository) {
//        this.userDataRepository = userDataRepository;
//        this.storageRepository = storageRepository;
//    }
//
////    private final String generalPath = "/Users/alexey/Desktop/Cloud";
//
//    @Value("${general.path}")
//    private String generalPath;
//
//    public UserData getUser(String username) {
//        UserData currentUser = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
//        if (currentUser.getIsEnable()) {
//            return currentUser;
//        } else {
//            System.out.println("User not found");
//            return null;
//        }
//
//    }
//
//    public List<FileRequest> getFilenamesFromStorage(long userId) {
//        List<Storage> listStorage = storageRepository.findByUserIdAndIsExist(userId, true);
//        List<FileRequest> files = new ArrayList<>();
//        for (Storage storage : listStorage) {
//            if (storage.getIsExist()) {
//                files.add(new FileRequest(storage.getFilename(), storage.getFileSize()));
//            }
//        }
//        return files;
//    }
//
//    public String getLocalDataPath(long userId) {
//        UserData currentUser = userDataRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
//        return currentUser.getDataPath();
//    }
//
//    public Boolean saveFile(Storage storage) {
//        var newStorage = storageRepository.save(storage);
//        return newStorage != null;
////        if (newStorage != null) {
////            return true;
////        }
////        return false;
//    }
//
//    //delete - пометить файл как удалённый
//    public Boolean deleteFile(String filename, long currentUserId) {
//        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, currentUserId);
//        UserData currentUser = userDataRepository.findById(currentUserId).orElseThrow(IllegalArgumentException::new);
//
//        String dataPath = generalPath + File.separator + currentUser.getDataPath();
//        String pathFile = dataPath + File.separator + filename;
//        String deleted = "_deleted at " + new Date();
//        File currentFile = new File(pathFile);
//        File deletedFile = new File(pathFile + deleted);
//        currentFile.renameTo(deletedFile);
//        currentStorage.setIsExist(false);
//        currentStorage.setFilename(filename + deleted);
//        if (!currentStorage.getIsExist()) {
//            System.out.println("Repo_deleted. Deleted");
//            return true;
//        }
//        System.out.println("Repo_deleted. No deleted");
//        return false;
//    }
//
//    // delete - удалить файл
////    public Boolean deleteFile(String filename, long currentUserId) {
////        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, currentUserId);
////        UserData currentUser = userDataRepository.findById(currentUserId).orElseThrow(IllegalArgumentException::new);
////        long currentId = currentStorage.getId();
////        System.out.println("Repo_deleted. Id " + currentId);
////
////        String dataPath = generalPath + File.separator + currentUser.getDataPath();
////        String pathFile = dataPath + File.separator + filename;
////        File currentFile = new File(pathFile);
////
////        storageRepository.deleteById(currentId);
////        if (!storageRepository.existsById(currentId)&&currentFile.delete()) {
////            System.out.println("Repo_deleted. Deleted");
////            return true;
////        }
////        System.out.println("Repo_deleted. No deleted");
////        return false;
////    }
//
//
//    public String renameFile(String currentFilename, String newFilename, long currentUserId, String username) {
//        Storage currentStorage = storageRepository.findByFilenameAndUserId(currentFilename, currentUserId);
//        UserData currentUser = getUser(username);
//
//        if (currentStorage.getIsExist() && (currentUser != null)) {
//
//            String dataPath = generalPath + File.separator + currentUser.getDataPath();
//            String pathFile = dataPath + File.separator + currentFilename;
//            String pathNewFile = dataPath + File.separator + newFilename;
//
//            File currentFile = new File(pathFile);
//            File newFile = new File(pathNewFile);
//
//            if (currentFile.renameTo(newFile)) {
//                currentStorage.setFilename(newFilename);
//                return newFilename;
//            }
//        }
//        return null;
//    }
//
//}

//    public List<FileRequest> getAllFiles(String username, int limit) {
//        System.out.println("Repo_listFiles. Username: " + username);
//        long userId = getUser(username).getId();
////        System.out.println("UserId: " + userId);
//        return getFilenamesFromStorage(userId)
//                .stream()
//                .limit(limit)
//                .sorted(Comparator.naturalOrder())
//                .collect(Collectors.toList());
//    }


//        return listStorage.stream()
//                .filter(x -> x.getIsExist())
//                .map(Storage::getFileName)
//                .sorted(Comparator.naturalOrder())
//                .collect(Collectors.toList());

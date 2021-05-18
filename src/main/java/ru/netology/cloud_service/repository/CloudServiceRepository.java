package ru.netology.cloud_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.model.Storage;
import ru.netology.cloud_service.model.UserData;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class CloudServiceRepository {

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private StorageRepository storageRepository;


    public UserData getUser(String username) {
        UserData currentUser = userDataRepository.findByUsername(username);
        if (currentUser.getIsEnable()) {
            return currentUser;
        } else {
            System.out.println("User not found");
            return null;
        }

    }

    public List<FileRequest> getFilenamesFromStorage(long userId) {
        List<Storage> listStorage = storageRepository.findByUserId(userId);
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
        if (newStorage != null) {
            return true;
        }
        return false;
    }

    public Boolean deleteFile(String filename) {
        Storage currentStorage = storageRepository.findByFilename(filename);
            currentStorage.setIsExist(false);
        if (!currentStorage.getIsExist()) {
            System.out.println("Repo_deleted. Deleted");
            return true;
        }
        System.out.println("Repo_deleted. No deleted");
        return false;
    }

    public String renameFile(String currentFilename, String newFilename) {
        Storage currentStorage = storageRepository.findByFilename(currentFilename);
        if (currentStorage.getIsExist()) {
            currentStorage.setFilename(newFilename);
            return newFilename;
        }
        return null;
    }





//    public Boolean deleteFile(String filename) {
//        Storage currentStorage = storageRepository.findByFilename(filename);
//        long currentId = currentStorage.getId();
//        System.out.println("Repo_deleted. Id " + currentId);
//        storageRepository.deleteById(currentId);
//        if (!storageRepository.existsById(currentId)) {
//            System.out.println("Repo_deleted. Deleted");
//            return true;
//        }
//        System.out.println("Repo_deleted. No deleted");
//        return false;
//    }

}


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

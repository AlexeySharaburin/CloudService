package ru.netology.cloud_service.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.model.Storage;
import ru.netology.cloud_service.model.UserData;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@Repository
@Transactional
public class CloudServiceRepository {

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private StorageRepository storageRepository;


    public UserData getUser(String username) {
        UserData currentUser = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
        if (currentUser.getIsEnable()) {
            return currentUser;
        } else {
            System.out.println("User not found");
            return null;
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
        if (newStorage != null) {
            return true;
        }
        return false;
    }

    //delete - помечать файл как удалённый
//    public Boolean deleteFile(String filename, long currentUserId) {
//        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, currentUserId);
//        currentStorage.setIsExist(false);
//        if (!currentStorage.getIsExist()) {
//            System.out.println("Repo_deleted. Deleted");
//            return true;
//        }
//        System.out.println("Repo_deleted. No deleted");
//        return false;
//    }

    // delete - удалить файл
    public Boolean deleteFile(String filename, long currentUserId) {
        Storage currentStorage = storageRepository.findByFilenameAndUserId(filename, currentUserId);
        UserData currentUser = userDataRepository.findById(currentUserId).orElseThrow(IllegalArgumentException::new);
        long currentId = currentStorage.getId();
        System.out.println("Repo_deleted. Id " + currentId);

        String dataPath = currentUser.getDataPath();
        String pathFile = dataPath + File.separator + filename;
        File currentFile = new File(pathFile);

        storageRepository.deleteById(currentId);
        if (!storageRepository.existsById(currentId)&&currentFile.delete()) {
            System.out.println("Repo_deleted. Deleted");
            return true;
        }
        System.out.println("Repo_deleted. No deleted");
        return false;
    }




    public String renameFile(String currentFilename, String newFilename, long currentUserId, String username) {
        Storage currentStorage = storageRepository.findByFilenameAndUserId(currentFilename, currentUserId);
        UserData currentUser = getUser(username);

        if (currentStorage.getIsExist() && (currentUser != null)) {

            String dataPath = currentUser.getDataPath();
            String pathFile = dataPath + File.separator + currentFilename;
            String pathNewFile = dataPath + File.separator + newFilename;

            File currentFile = new File(pathFile);
            File newFile = new File(pathNewFile);

            if (currentFile.renameTo(newFile)) {
                currentStorage.setFilename(newFilename);
                return newFilename;
            }
        }
        return null;
    }

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

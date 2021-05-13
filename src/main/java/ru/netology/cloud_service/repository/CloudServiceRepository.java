package ru.netology.cloud_service.repository;


import org.apache.logging.log4j.util.PropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.FileRequest;
import ru.netology.cloud_service.model.Storage;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class CloudServiceRepository {

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Autowired
    private StorageRepository storageRepository;


    public List<FileRequest> listFiles(String username) {
        System.out.println("Repo");
        List<Storage> listStorage = storageRepository.findByUsername(username);
        List<FileRequest> files = new ArrayList<>();
        for (Storage storage : listStorage) {
            if (storage.getIsExist()) {
                files.add(new FileRequest(storage.getFileName(), storage.getFileSize()));
            }
        }
        return files.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
//        return listStorage.stream()
//                .filter(x -> x.getIsExist())
//                .map(Storage::getFileName)
//                .sorted(Comparator.naturalOrder())
//                .collect(Collectors.toList());
    }
}

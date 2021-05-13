package ru.netology.cloud_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.Storage;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, String> {

    List<Storage> findByUsername(String username);
}

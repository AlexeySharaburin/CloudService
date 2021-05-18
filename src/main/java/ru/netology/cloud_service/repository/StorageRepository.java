package ru.netology.cloud_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.Storage;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    List<Storage> findByUserId(long userId);

    @Query("select s from Storage s where s.filename = :filename")
    Storage findByFilename(@Param("filename") String filename);

}

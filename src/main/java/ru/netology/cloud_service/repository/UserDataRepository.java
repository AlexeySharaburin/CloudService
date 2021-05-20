package ru.netology.cloud_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.UserData;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

//    @Override
//    Optional<UserData> findById(Long aLong);

    Optional<UserData> findByUsername(String username);

    Optional<UserData> findById(long id);

}

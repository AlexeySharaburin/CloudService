package ru.netology.cloud_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.UserData;

@Repository
public interface UserRepository extends JpaRepository<UserData, Long> {
    UserData findByUsername(String username);
}

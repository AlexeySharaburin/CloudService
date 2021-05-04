package ru.netology.cloud_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloud_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}

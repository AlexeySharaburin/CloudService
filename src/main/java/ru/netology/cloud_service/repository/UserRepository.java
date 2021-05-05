package ru.netology.cloud_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername( String username);
}

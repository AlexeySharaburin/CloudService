package ru.netology.cloud_service.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.User;


@Repository
public interface UserDaoRepository extends CrudRepository<User, Long> {
}
